# GoSung: ROS2 기반 실외 자율주행 배달 로봇

- 프로젝트 기간: 2023.06.10 ~ 2024.12.15  
- 참여인원: 6명  

<br>

## 🎥 프로젝트 소개
[![GoSung Demo](https://img.youtube.com/vi/tpwEQPsNEKI/0.jpg)](https://youtu.be/tpwEQPsNEKI)
➡ 영상 클릭 시, youtube 재생  

**GoSung**은 가파르게 상승하는 배달비 문제와 특정 구역의 배달 비효율성을 해결하기 위해 개발된 **실외 자율주행 배달 로봇 시스템**입니다.  

- Android 앱, Firebase 클라우드 서버, **자율주행 로봇(Jetson Nano & STM32)** 연동 통합 서비스 생태계 구축  
- ROS2 Navigation2 Stack 기반 자율주행  
- YOLOv5 객체 인식  
- **다중 센서 퓨전(EKF)** 기반 정밀 위치 추정  
- **FreeRTOS(CMSIS V2-RTOS)**로 안전 중심의 실시간 임베디드 제어  

<br>

## 🔧 주요 기능
- 📱 **End-to-End 서비스 아키텍처**: 앱(Android) → Firebase 서버 → 로봇 배달 임무까지 전체 프로세스 연동  
- 🛡️ **실시간 안전 제어**: SRF08 초음파 센서 + FreeRTOS 우선순위 기반 태스크로 비상정지 신뢰성 확보  
- 🧭 **ROS2 자율주행 내비게이션**: LIDAR, 카메라, GPS, IMU, 엔코더 융합으로 안정적 주행  
- 🤖 **분산 컴퓨팅 시스템**: Jetson Nano(ROS2) ↔ STM32(RTOS) 역할 분담으로 안정성 극대화  

<br>

## 🚀 전체 실행 순서

GoSung 로봇의 자율주행 시스템을 구성하는 모든 ROS2 노드를 실행하는 절차입니다.  
각 명령어는 별도의 터미널에서 순차적으로 실행되어야 합니다.  

```bash
# 1. 3D 카메라(Intel RealSense) 드라이버 및 포인트 클라우드 생성 노드 실행
ros2 launch realsense2_camera demo_pointcloud_launch.py

# 2. 2D LIDAR 드라이버 실행
ros2 launch lakibeam1 lakibeam1_scan.launch.py

# 3. GPS 및 IMU 데이터 퍼블리셔 실행
ros2 run gps_imu gps_imu_publisher

# 4. AI 기반 객체 인식 노드 실행
ros2 run yolov5 detect

# 5. 로봇의 각 센서 좌표계(TF) 정의 및 브로드캐스팅
ros2 run tf_broadcast tf_transform

# 6. 다중 센서 데이터 융합(위치 추정) 노드 실행
ros2 run sensor_fusion sensor_fusion

# 7. ROS2 Navigation2 스택 실행
ros2 launch navigation bringup_launch.py

# 8. 웨이포인트(경유지) 추종 어플리케이션 실행
ros2 run follow_waypoint follow_waypoints

# 9. (디버깅용) 수동 제어 및 테스트용 속도 명령 퍼블리셔 실행
ros2 run cmd_vel_pub simple_publisher
