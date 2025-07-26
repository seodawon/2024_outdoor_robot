import rclpy # ROS 2 Python 클라이언트 라이브러리 import
from rclpy.node import Node # ROS 2 노드를 만들기 위한 클래스
from visualization_msgs.msg import Marker # Rviz에서 시각화 할 수 있는 마커 메시지 
from sensor_msgs.msg import NavSatFix # IMU 센서 데이터를 표현하는 메시지 타입 
# from geometry_msgs.msg import Quaternion, Pose, Twist, TransformStamped # 로봇의 자세 및 위치 변환을 표현하는 메시지 타입
from nav_msgs.msg import Odometry # 로봇의 위치 및 속도를 표현하는 메시지 타입 
from tf2_ros import TransformBroadcaster # ROS 2에서 TF 변환을 PUBLISH하기 위한 브로드캐스터 클래스

import serial # 시리얼 포트를 통해 데이터를 읽기 위한 라이브러리
import math # 수학적 계산을 위한 라이브러리3

class GPSPublisher(Node):
    def __init__(self): # 노드의 생성자 메서드 -> 노드의 초기화 작업 수행
        super().__init__('gps_publisher') # 노드 이름 설정 
        # 'gps/marker' 토픽으로 'Marker' 메시지를 퍼블리시할 PUBLISHER 생성 -> Rviz 시각화
        # self.publisher = self.create_publisher(Marker, '/gps/marker', 10)
        # '/imu0' 토픽으로 'Imu' 메시지를 퍼블리시할 PUBLISHER 생성 -> IMU 데이터
        self.gps_publisher = self.create_publisher(NavSatFix, '/fix', 10)
        # 0.01초 주기로 'timer_callback' 메서드 호출
        self.timer = self.create_timer(0.01, self.timer_callback)
        # '/dev/ttyUSB0' 포트에서 115200 baudrate로 시리얼 포트 설정
        self.serial_port = serial.Serial('/dev/ttyUSB0', baudrate=115200)
        # TF 변환을 PUBLISH 할 브로드캐스터 초기화
        # self.tf_broadcaster = TransformBroadcaster(self)


    def timer_callback(self):
        latitude,longitude=0.0,0.0
        # IMU 센서와 포트 통신이 정상적으로 연결되어있는지 확인 
        if self.serial_port:
            # 직렬 포트로부터 한 줄의 데이터 출력
            line = self.serial_port.readline()
            # 데이터를 'UTF-8'로  디코딩하여 문자열로 변환
            # 'errors=ignore'로 디코딩 중 오류가 발생해도 무시하고 계속 진행
            # 'strip()'를 통해 앞 뒤의 공백이나 개행 문자 제거
            line = line.decode('utf-8', errors='ignore').strip()
            # '*' 문자로 시작한다면, 해당 문자를 제거 -> 데이터의 시작을 나타내는 기호일 가능성이 있음
            if line.startswith('*'):
                line = line[1:]
            try:
                # 데이터를 ','로 분리 -> (roll,pitch,yaw) 형식을 분리하여 데이터 추출
                parts = line.split(',')
                # 데이터가 3개로 나뉜 경우, 'float'으로 변환하여 변수에 할당 -> 오일러각 (회전 각도)
                if len(parts) > 10:
                    latitude = float(parts[9])
                    longitude = float(parts[10])
                #   roll,pitch,yaw,ax,ay,az,lx,ly,lz = map(float, parts)
                print("latitude: ",latitude)
                print("longitude: ",longitude)
                # print(roll)
                # print(pitch)
                # print(yaw)
                # print("-----------") # 구분을 위해 출력하는 줄

                gps_msg = NavSatFix() # IMU 메시지 생성 -> IMU 데이터 발행
                gps_msg.header.frame_id = 'base_link' # 'base_link' 프레임 기준으로 IMU 데이터 설정
                gps_msg.header.stamp = self.get_clock().now().to_msg() # 현재 시간 설정
                gps_msg.latitude = latitude
                gps_msg.longitude = longitude

                self.gps_publisher.publish(gps_msg)

            except ValueError as e:
                self.get_logger().error(f"Failed to convert to float: {line}, Error: {str(e)}")   


def main(args=None):
	rclpy.init(args=args)

	print("Starting gps_publisher..")

	node = GPSPublisher()

	try:
		rclpy.spin(node)

	finally:
		node.destroy_node()
		rclpy.shutdown()


if __name__ == '__main__':
	main()               