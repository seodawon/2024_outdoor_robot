import rclpy
import firebase_admin
from rclpy.node import Node
from geometry_msgs.msg import PoseStamped, Quaternion
from nav2_simple_commander.robot_navigator import BasicNavigator, TaskResult
from sensor_msgs.msg import NavSatFix, Imu
from firebase_admin import credentials, db
import re
import time
from pyproj import Transformer


class NavigationNode(Node):
    def __init__(self):
        super().__init__('navigation_node')

        # Firebase 초기화
        cred = credentials.Certificate("/home/seunghun/Downloads/dbtest-c6461-firebase-adminsdk-rfe4b-2fca47191c.json")
        firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://dbtest-admin.firebaseio.com/'
        })

        # 선반 위치와 배송지 초기화 
        self.shelf_positions = {"Cafe": [0, 0]}
        self.shipping_destinations = {"enbong": [0, 0]}
        self.transformer = Transformer.from_crs("epsg:4326", "epsg:32652")  # WGS84 -> UTM Zone 52N
        self.origin_lat = 37.630982
        self.origin_lon = 127.054222
        self.origin_x, self.origin_y = self.transformer.transform(self.origin_lat, self.origin_lon)
        
        # 네비게이터 초기화
        self.navigator = BasicNavigator()

        # GPS 데이터 초기화
        self.current_lat = 0.0
        self.current_lon = 0.0
        self.current_yaw = Quaternion(x=0.0, y=0.0, z=0.0, w=1.0)


        # GPS 토픽 구독
        self.create_subscription(NavSatFix, '/fix', self.gps_callback, 10)

        #IMU 토픽 구독
        self.create_subscription(Imu, '/imu', self.imu_callback, 10)

        # Firebase 데이터 초기화
        self.initialize_positions()

        # 초기 위치 설정 (GPS 데이터 수신 대기)
        self.set_initial_pose()

        # 네비게이션 실행
        self.navigate()

    def imu_callback(self, msg):
        self.get_logger().info(f"GPS callback triggered: {msg}")
        self.current_yaw = msg.orientation
        # self.get_logger().info(f"Received GPS - Latitude: {self.current_lat}, Longitude: {self.current_lon}")
        # print(msg.latitude)

    def gps_callback(self, msg):
        # self.get_logger().info(f"GPS callback triggered: {msg}")
        self.current_lat = msg.latitude
        self.current_lon = msg.longitude
        self.get_logger().info(f"Received GPS - Latitude: {self.current_lat}, Longitude: {self.current_lon}")    

    def get_coordinates_from_firebase(self):
        data = None
        while data is None or data == "[Lat 0.0 Lon 0.0, Lat 0.0 Lon 0.0]":
            data = db.reference('/admin/id/da1218/information/ApptoRobot').get()
            if not data or data == "[Lat 0.0 Lon 0.0, Lat 0.0 Lon 0.0]":
                self.get_logger().info("Waiting for valid Firebase data...")
                time.sleep(1)
        return data

    def parse_coordinates(self, data):
        coordinates = re.findall(r"Lat ([\d.]+) Lon ([\d.]+)", data)
        a, b = float(coordinates[0][0]), float(coordinates[0][1])
        c, d = float(coordinates[1][0]), float(coordinates[1][1])
        a_x, b_y = self.transformer.transform(a, b)
        ros_ax = a_x - self.origin_x
        ros_by = b_y - self.origin_y
        c_x, d_y = self.transformer.transform(c, d)
        ros_cx = c_x - self.origin_x
        ros_dy = d_y - self.origin_y
        self.get_logger().info(f"Parsed coordinates: a={a}, b={b}, c={c}, d={d}")
        return ros_ax, ros_by, ros_cx, ros_dy

    def initialize_positions(self):
        data = self.get_coordinates_from_firebase()
        a, b, c, d = self.parse_coordinates(data)

        self.shelf_positions["Cafe"] = [a, b]
        self.shipping_destinations["enbong"] = [c, d]

        self.get_logger().info(f"Shelf Positions: {self.shelf_positions}")
        self.get_logger().info(f"Shipping Destinations: {self.shipping_destinations}")

    def set_initial_pose(self):
        # GPS 데이터 수신 대기
        while self.current_lat is None or self.current_lon is None:
            self.get_logger().info("Waiting for GPS data...")
            time.sleep(0.5)

        gps_x, gps_y = self.transformer.transform(self.current_lat, self.current_lon)
        ros_x = gps_x - self.origin_x
        ros_y = gps_y - self.origin_y

        initial_pose = PoseStamped()
        initial_pose.header.frame_id = 'map'
        initial_pose.header.stamp = self.navigator.get_clock().now().to_msg()
        initial_pose.pose.position.x = ros_x
        initial_pose.pose.position.y = ros_y
        initial_pose.pose.orientation = self.current_yaw

        self.navigator.setInitialPose(initial_pose)
        self.navigator.waitUntilNav2Active()

    def navigate(self):
        request_item_location = 'Cafe' #Cafe
        request_destination = 'enbong'

        # 선반으로 이동
        shelf_item_pose = PoseStamped()
        shelf_item_pose.header.frame_id = 'map'
        shelf_item_pose.header.stamp = self.navigator.get_clock().now().to_msg()
        shelf_item_pose.pose.position.x = self.shelf_positions[request_item_location][0]
        shelf_item_pose.pose.position.y = self.shelf_positions[request_item_location][1]
        shelf_item_pose.pose.orientation.x = 0.0
        shelf_item_pose.pose.orientation.y = 0.0
        shelf_item_pose.pose.orientation.z = -0.6677065661621234
        shelf_item_pose.pose.orientation.w = 0.7444245707282813

        self.get_logger().info(f"Navigating to {request_item_location}")
        self.navigator.goToPose(shelf_item_pose)

        while not self.navigator.isTaskComplete():
            feedback = self.navigator.getFeedback()
            if feedback:
                eta = feedback.estimated_time_remaining.sec
                self.get_logger().info(f"ETA to {request_item_location}: {eta} seconds")

        result = self.navigator.getResult()
        if result == TaskResult.SUCCEEDED:
            self.get_logger().info(f"Arrived at {request_item_location}, heading to {request_destination}")

            # 배송지로 이동
            shipping_pose = PoseStamped()
            shipping_pose.header.frame_id = 'map'
            shipping_pose.header.stamp = self.navigator.get_clock().now().to_msg()
            shipping_pose.pose.position.x = self.shipping_destinations[request_destination][0]
            shipping_pose.pose.position.y = self.shipping_destinations[request_destination][1]
            shelf_item_pose.pose.orientation.x = 0.0
            shelf_item_pose.pose.orientation.y = 0.0
            shelf_item_pose.pose.orientation.z = 0.54
            shelf_item_pose.pose.orientation.w = 0.84


            self.navigator.goToPose(shipping_pose)
            while not self.navigator.isTaskComplete():
                pass

            self.get_logger().info("Task completed successfully!")
        elif result == TaskResult.FAILED:
            self.get_logger().error("Navigation task failed!")
        elif result == TaskResult.CANCELED:
            self.get_logger().warn("Navigation task canceled!")


def main(args=None):
    rclpy.init(args=args)
    node = NavigationNode()

    try:
        rclpy.spin(node)
    except KeyboardInterrupt:
        node.get_logger().info("Node interrupted by user.")
    finally:
        node.destroy_node()
        rclpy.shutdown()


if __name__ == '__main__':
    main()
