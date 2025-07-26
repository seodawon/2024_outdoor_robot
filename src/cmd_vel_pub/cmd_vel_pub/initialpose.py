import rclpy
from rclpy.node import Node
from geometry_msgs.msg import PoseWithCovarianceStamped
import firebase_admin
from firebase_admin import credentials, db

# Firebase 초기화
cred = credentials.Certificate("/home/seunghun/Downloads/latlong-f734e-firebase-adminsdk-3mt3w-ee5eaa14fa.json")  # 서비스 계정 키 경로
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://latlong-f734e-default-rtdb.firebaseio.com/'  # Firebase 데이터베이스 URL
})

class InitialPosePublisher(Node):
    def __init__(self):
        super().__init__('initial_pose_publisher')
        self.publisher_ = self.create_publisher(PoseWithCovarianceStamped, '/initialpose', 10)
        self.create_subscription()

    def create_subscription(self):
        # Firebase에서 'goal_pose' 노드의 변화를 실시간으로 감지
        ref = db.reference('goal_pose')
        ref.listen(self.on_goal_pose_change)

    def on_goal_pose_change(self, event):
        """데이터베이스의 값이 변경될 때마다 호출되는 콜백 함수"""
        # 이벤트에서 새 데이터 가져오기
        data = event.data
        if data:
            # 데이터가 dict 형식일 때
            if isinstance(data, dict):
                lat = self.get_float_value(data, 'lat')
                lon = self.get_float_value(data, 'lon')
                self.publish_initial_pose(lat, lon)
            # 데이터가 int나 float 형식일 때
            # elif isinstance(data, (int, float)):
            #     lat = float(data)
            #     lon = float(data)  # 예시로 lat과 lon을 동일하게 설정
            #     self.publish_initial_pose(lat, lon)
            else:
                self.get_logger().error('Unexpected data format received from Firebase.')

    def get_float_value(self, data, key):
        """데이터에서 키를 가져오고 float 값으로 변환"""
        try:
            return float(data.get(key, 0.0))
        except (ValueError, TypeError):
            self.get_logger().error(f"Invalid value for {key}. Using default 0.0.")
            return 0.0

    def publish_initial_pose(self, lat, lon):
        pose_msg = PoseWithCovarianceStamped()
        
        # 헤더 설정
        pose_msg.header.stamp = self.get_clock().now().to_msg()
        pose_msg.header.frame_id = "map"  # 맵 좌표계 기준으로 설정

        # 로봇의 초기 위치와 방향 설정
        pose_msg.pose.pose.position.x = lat
        pose_msg.pose.pose.position.y = lon
        pose_msg.pose.pose.position.z = 0.0  # 2D 공간에서 z는 0

        # 로봇의 방향 설정 (쿼터니언)
        pose_msg.pose.pose.orientation.x = 0.0
        pose_msg.pose.pose.orientation.y = 0.0
        pose_msg.pose.pose.orientation.z = 0.0
        pose_msg.pose.pose.orientation.w = 1.0  # 고정된 w 값 (0도 회전)

        # 공분산 행렬 설정 (위치 추정의 불확실성)
        pose_msg.pose.covariance = [
            0.25, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.25, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.06853891909122467
        ]


        # 초기 위치를 /initialpose에 발행
        self.publisher_.publish(pose_msg)
        self.get_logger().info(f'Initial pose published: x={lat}, y={lon}')

def main(args=None):
    rclpy.init(args=args)
    node = InitialPosePublisher()
    rclpy.spin(node)  # 노드가 계속 실행됨
    node.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()
