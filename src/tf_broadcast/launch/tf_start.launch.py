from launch import LaunchDescription
from launch.actions import IncludeLaunchDescription
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch.substitutions import PathJoinSubstitution
from launch_ros.substitutions import FindPackageShare
from launch_ros.actions import Node

def generate_launch_description():


    tf_transform = Node(
        package='my_tf_ros2_test_pkg',
        executable='tf_transform',
        name='tf_transform',
        output='screen',
        emulate_tty=True,
        parameters=[{'use_sim_time': True}]
    )

    odom_node = Node(
        package='my_tf_ros2_test_pkg',
        executable='odom_node',
        name='odom_node',
        output='screen',
        emulate_tty=True,
        parameters=[{'use_sim_time': True}]
    )

    clock_node = Node(
        package='my_tf_ros2_test_pkg',
        executable='clock_node',
        name='clock_node',
        output='screen',
        emulate_tty=True,
    )

    return LaunchDescription([
        tf_transform,
        odom_node,
        clock_node,
    ])