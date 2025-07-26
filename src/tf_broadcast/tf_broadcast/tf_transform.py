import rclpy
from rclpy.node import Node
from rclpy.qos import ReliabilityPolicy, DurabilityPolicy, QoSProfile
import tf2_ros
from sensor_msgs.msg import Imu, NavSatFix
from geometry_msgs.msg import TransformStamped, Quaternion
from nav_msgs.msg import Odometry
import math


class TfBroadcast(Node):
    def __init__(self):
        super().__init__('tf_broadcast')

        self.t_map_to_odom_broadcaster = tf2_ros.TransformBroadcaster(self)

        self.t_odom_to_base_broadcaster = tf2_ros.TransformBroadcaster(self)
        # self.t_odom_org_to_base_broadcaster = tf2_ros.TransformBroadcaster(self)

        self.t_base_to_imu_broadcaster = tf2_ros.TransformBroadcaster(self)

        self.t_base_to_lidar_2D_broadcaster = tf2_ros.TransformBroadcaster(self)

        self.timer = self.create_timer(0.01, self.tf_transform)

        self.imu_data = None
        self.odom_data = None
        # self.odom_org_data = None

        self.imu_subscriber = self.create_subscription(
            Imu,
            '/imu',
            self.imu_callback,
            QoSProfile(depth=10, durability=DurabilityPolicy.VOLATILE, reliability=ReliabilityPolicy.BEST_EFFORT)
        )

        self.odom_subscriber = self.create_subscription(
            Odometry,
            '/odom', # 핕터 odom
            self.odom_callback,
            10
        )

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

    def imu_callback(self, msg):
        self.imu_data = msg
        self.get_logger().debug('imu_data: "%s"' % str(self.imu_data))
        # quaternion = self.imu_data.orientation


    def odom_callback(self, msg):
        self.odom_data = msg
        self.get_logger().debug(f'Received Odometry data: {self.odom_data}')

    def tf_transform(self):

        now = self.get_clock().now().to_msg()

        if self.imu_data is None:
            self.get_logger().warn('IMU data is not available yet.')
        else:
            quaternion = self.imu_data.orientation

        if self.odom_data is None:
            self.get_logger().warn('Odometry data is not available yet.')
            odom_position = TransformStamped().transform.translation
            odom_orientation = Quaternion(x=0.0, y=0.0, z=0.0, w=1.0)
        else:
            odom_position = self.odom_data.pose.pose.position
            odom_orientation = self.odom_data.pose.pose.orientation

        # if self.odom_org_data is None:
        #     self.get_logger().warn('odom_org_data is not available yet.')
        #     odom_org_position = TransformStamped().transform.translation
        # else:
        #     odom_org_position = self.odom_org_data.pose.pose.position

        # map -> odom
        t_map_to_odom = TransformStamped()
        t_map_to_odom.header.stamp = now
        t_map_to_odom.header.frame_id = 'map'
        t_map_to_odom.child_frame_id = 'odom'
        t_map_to_odom.transform.translation.x = 0.0
        t_map_to_odom.transform.translation.y = 0.0
        t_map_to_odom.transform.translation.z = 0.0
        # t_map_to_odom.transform.rotation.x = 0.0
        # t_map_to_odom.transform.rotation.y = 0.0
        # t_map_to_odom.transform.rotation.z = 0.0
        # t_map_to_odom.transform.rotation.w = 1.0
        # t_map_to_odom.transform.rotation = quaternion
        self.t_map_to_odom_broadcaster.sendTransform(t_map_to_odom)

        # odom -> base_link
        t_odom_to_base = TransformStamped()
        t_odom_to_base.header.stamp = now
        t_odom_to_base.header.frame_id = 'odom' #원래 /odom
        t_odom_to_base.child_frame_id = 'base_link'
        t_odom_to_base.transform.translation.x = odom_position.x
        t_odom_to_base.transform.translation.y = odom_position.y
        t_odom_to_base.transform.translation.z = 0.0
        # t_odom_to_base.transform.rotation.x = odom_orientation.x
        # t_odom_to_base.transform.rotation.y = odom_orientation.y
        # t_odom_to_base.transform.rotation.z = odom_orientation.z
        # t_odom_to_base.transform.rotation.w = odom_orientation.w
        t_odom_to_base.transform.rotation = odom_orientation
        self.t_odom_to_base_broadcaster.sendTransform(t_odom_to_base)

        # # odom -> base_link
        # t_odom_org_to_base = TransformStamped()
        # t_odom_org_to_base.header.stamp = now
        # t_odom_org_to_base.header.frame_id = 'odom' #원래 /odom
        # t_odom_org_to_base.child_frame_id = 'odom_org'
        # t_odom_org_to_base.transform.translation.x = odom_org_position.x
        # t_odom_org_to_base.transform.translation.y = odom_org_position.y
        # t_odom_org_to_base.transform.translation.z = 0.0
        # self.t_odom_org_to_base_broadcaster.sendTransform(t_odom_org_to_base)

        # base_link -> imu_link
        t_base_to_imu = TransformStamped()
        t_base_to_imu.header.stamp = now
        t_base_to_imu.header.frame_id = 'base_link'
        t_base_to_imu.child_frame_id = 'imu' #imu_link 였음
        t_base_to_imu.transform.translation.x = 0.0
        t_base_to_imu.transform.translation.y = 0.0
        t_base_to_imu.transform.translation.z = 0.0
        # t_base_to_imu.transform.rotation = quaternion #원래 주석이였음
        self.t_base_to_imu_broadcaster.sendTransform(t_base_to_imu)

        # base_link -> laser_link_2D
        t_base_to_lidar_2D = TransformStamped()
        t_base_to_lidar_2D.header.stamp = now
        t_base_to_lidar_2D.header.frame_id = 'base_link'
        t_base_to_lidar_2D.child_frame_id = 'laser'
        t_base_to_lidar_2D.transform.translation.x = 0.0
        t_base_to_lidar_2D.transform.translation.y = 0.0
        t_base_to_lidar_2D.transform.translation.z = 0.6
        # quaternion = self.euler_to_quaternion(0.0, 0.0, 3.14159)  # Roll, Pitch, Yaw (라디안 단위)
        # t_base_to_lidar_2D.transform.rotation = quaternion
        self.t_base_to_lidar_2D_broadcaster.sendTransform(t_base_to_lidar_2D)


def main(args=None):
    rclpy.init(args=args)
    tf_broadcast_node = TfBroadcast()
    rclpy.spin(tf_broadcast_node)
    tf_broadcast_node.destroy_node()
    rclpy.shutdown()

if __name__ == '__main__':
    main()
