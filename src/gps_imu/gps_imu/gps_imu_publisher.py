import rclpy
from rclpy.node import Node
from sensor_msgs.msg import NavSatFix, Imu
from geometry_msgs.msg import Quaternion, PoseWithCovarianceStamped
from nav_msgs.msg import Odometry
from pyproj import Transformer

import serial
import math


class GPSIMUPublisher(Node):
    def __init__(self):
        super().__init__('gps_imu_publisher')

        # GPS와 IMU 토픽 퍼블리셔 생성
        self.gps_publisher = self.create_publisher(NavSatFix, '/fix', 10)
        self.imu0_publisher = self.create_publisher(Imu, '/imu', 10)
        self.odom0_publisher = self.create_publisher(Odometry, '/odom', 10)
        # self.pose0_publisher = self.create_publisher(PoseWithCovarianceStamped, '/pose0', 10)

        # 타이머 설정
        self.timer = self.create_timer(0.01, self.timer_callback)

        # 시리얼 포트 설정 (GPS와 IMU를 같은 포트에서 읽는다고 가정)
        self.serial_port = serial.Serial('/dev/ttyUSB0', baudrate=115200)
        # WGS84 -> UTM Zone 52N
        self.transformer = Transformer.from_crs("epsg:4326", "epsg:32652")

        # 기준점 설정 (GPS 기준)
        self.origin_lat = 37.630982
        self.origin_lon = 127.054222
        self.origin_x, self.origin_y = self.transformer.transform(self.origin_lat, self.origin_lon)

    def euler_to_quaternion(self, roll, pitch, yaw):
        roll = math.radians(roll)
        pitch = math.radians(pitch)
        yaw = math.radians(yaw)

        cy = math.cos(yaw * 0.5)
        sy = math.sin(yaw * 0.5)
        cp = math.cos(pitch * 0.5)
        sp = math.sin(pitch * 0.5)
        cr = math.cos(roll * 0.5)
        sr = math.sin(roll * 0.5)

        qx = sr * cp * cy - cr * sp * sy
        qy = cr * sp * cy + sr * cp * sy
        qz = cr * cp * sy - sr * sp * cy
        qw = cr * cp * cy + sr * sp * sy

        return Quaternion(x = qx, y = qy, z = qz, w = qw)

    def timer_callback(self):
        if self.serial_port:
            line = self.serial_port.readline()
            line = line.decode('utf-8', errors='ignore').strip()

            line = line.replace('\x00', '')
            if line.startswith("*", 0):
                line = line[1:]
            try:
                parts = line.split(',')
                if len(parts) == 11:  # 기대하는 데이터 개수 확인
                    roll = float(parts[0])
                    pitch = float(parts[1])
                    yaw = float(parts[2])
                    ax = float(parts[3])
                    ay = float(parts[4])
                    az = float(parts[5])
                    lx = float(parts[6])
                    ly = float(parts[7])
                    lz = float(parts[8])
                    latitude = float(parts[9])
                    longitude = float(parts[10])
                    print(line)
                    # roll = 0.0
                    # pitch = 0.0
                    # yaw = 0.0
                    # ax = 0.0
                    # ay = 0.0
                    # az = 0.0
                    # lx = 0.0
                    # ly = 0.0
                    # # lz = 0.0
                    # latitude = 37.632104  #카페
                    # longitude = 127.054712
                    # latitude = 37.630979 #은봉관
                    # longitude = 127.054118

                    gps_msg = NavSatFix()
                    gps_msg.header.frame_id = 'base_link'
                    gps_msg.header.stamp = self.get_clock().now().to_msg()
                    gps_msg.latitude = latitude
                    gps_msg.longitude = longitude
                    self.gps_publisher.publish(gps_msg)

                    # offset 하는거
                    offset_yaw = yaw 
                    # IMU 데이터
                    quaternion = self.euler_to_quaternion(0.0, 0.0, -offset_yaw)

                    imu_msg = Imu()
                    imu_msg.header.frame_id = 'base_link'
                    imu_msg.header.stamp = self.get_clock().now().to_msg()
                    imu_msg.orientation = quaternion
                    imu_msg.angular_velocity.x = 0.0
                    imu_msg.angular_velocity.y = 0.0
                    imu_msg.angular_velocity.z = az
                    imu_msg.linear_acceleration.x = 0.0
                    imu_msg.linear_acceleration.y = 0.0
                    imu_msg.linear_acceleration.z = lz
                    self.imu0_publisher.publish(imu_msg)

                    gps_x, gps_y = self.transformer.transform(latitude, longitude)
                    ros_x = gps_x - self.origin_x
                    ros_y = gps_y - self.origin_y

                    odom = Odometry()
                    odom.header.stamp = self.get_clock().now().to_msg()
                    odom.header.frame_id = "odom"
                    odom.pose.pose.position.x = ros_x
                    odom.pose.pose.position.y = ros_y
                    odom.pose.pose.position.z = 0.0
                    odom.pose.pose.orientation = quaternion
                    odom.pose.covariance = [
                        0.7, 0.0, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.7, 0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.7, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.7, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.7, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0, 0.7
                    ]
                    odom.twist.twist.angular.z = az
                    odom.twist.twist.linear.x = lx
                    self.odom0_publisher.publish(odom)

                    # pose = PoseWithCovarianceStamped()
                    # pose.header.frame_id = 'odom'
                    # pose.pose.pose.position.x = ros_x
                    # pose.pose.pose.position.y = ros_y
                    # pose.pose.pose.position.z = 0.0
                    # pose.pose.pose.orientation = quaternion
                    # pose.pose.covariance = [
                    #     0.7, 0.0, 0.0, 0.0, 0.0, 0.0,
                    #     0.0, 0.7, 0.0, 0.0, 0.0, 0.0,
                    #     0.0, 0.0, 0.7, 0.0, 0.0, 0.0,
                    #     0.0, 0.0, 0.0, 0.7, 0.0, 0.0,
                    #     0.0, 0.0, 0.0, 0.0, 0.7, 0.0,
                    #     0.0, 0.0, 0.0, 0.0, 0.0, 0.7
                    # ]
                    # self.pose0_publisher.publish(pose)

            except ValueError as e:
                self.get_logger().error(f"Failed to process line: {line}, Error: {str(e)}")

def main(args=None):
    rclpy.init(args=args)
    node = GPSIMUPublisher()
    try:
        rclpy.spin(node)
    finally:
        node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    main()
