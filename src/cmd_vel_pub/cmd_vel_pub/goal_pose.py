import rclpy
from rclpy.node import Node
from geometry_msgs.msg import PoseStamped
import firebase_admin
from firebase_admin import credentials, db

# Firebase 초기화
cred = credentials.Certificate("/home/seunghun/Downloads/latlong-f734e-firebase-adminsdk-3mt3w-ee5eaa14fa.json")  # 서비스 계정 키 경로
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://latlong-f734e-default-rtdb.firebaseio.com/'  # Firebase 데이터베이스 URL
})

class GoalPublisher(Node):
    def __init__(self):
        super().__init__('goal_publisher')
        self.publisher_ = self.create_publisher(PoseStamped, '/goal_pose', 10)
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
                self.publish_goal(lat, lon)
            # 데이터가 int나 float 형식일 때
            # elif isinstance(data, (int, float)):
            #     lat = float(data)
            #     lon = float(data)  # 예시로 lat과 lon을 동일하게 설정
            #     self.publish_goal(lat, lon)
            else:
                self.get_logger().error('Unexpected data format received from Firebase.')

    def get_float_value(self, data, key):
        """데이터에서 키를 가져오고 float 값으로 변환"""
        try:
            return float(data.get(key, 0.0))
        except (ValueError, TypeError):
            self.get_logger().error(f"Invalid value for {key}. Using default 0.0.")
            return 0.0

    def publish_goal(self, lat, lon):
        goal = PoseStamped()
        goal.header.frame_id = "map"  # 맵 좌표계 기준으로 설정
        goal.header.stamp = self.get_clock().now().to_msg()

        # Firebase에서 가져온 목표 위치 설정
        goal.pose.position.x = lat
        goal.pose.position.y = lon
        goal.pose.position.z = 0.0

        # 목표 방향을 고정된 값으로 설정 (예: 0도 회전)
        goal.pose.orientation.x = 0.0
        goal.pose.orientation.y = 0.0
        goal.pose.orientation.z = 0.0
        goal.pose.orientation.w = 1.0  # 고정된 쿼터니언 값 (0도 회전)

        self.publisher_.publish(goal)
        self.get_logger().info(f'Goal pose published: x={lat}, y={lon}')

def main(args=None):
    rclpy.init(args=args)
    node = GoalPublisher()
    rclpy.spin(node)  # 노드가 계속 실행되며 Firebase 변화에 반응
    node.destroy_node()
    rclpy.shutdown()