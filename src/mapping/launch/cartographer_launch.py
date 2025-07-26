import os
from  ament_index_python.packages import get_package_share_directory
from launch import LaunchDescription
from launch.actions import SetEnvironmentVariable
from launch_ros.actions import Node
from launch.substitutions import LaunchConfiguration
from launch.actions import DeclareLaunchArgument
from launch.conditions import IfCondition


def generate_launch_description():
    use_sim_time = LaunchConfiguration('use_sim_time', default='False')
    cartographer_config_dir = LaunchConfiguration('cartographer_config_dir', 
        default=os.path.join(get_package_share_directory('mapping') , 'config'))
    configuration_basename = LaunchConfiguration('configuration_basename', default='cartographer_bag.lua')
    resolution = LaunchConfiguration('resolution', default='0.05') # 지도의 해상도 설정 기본값 0.05 5cm 값이 작을수록 정밀한 지도 생성 하지만 더 많은 계산 처리
    publish_period_sec = LaunchConfiguration('publish_period_sec', default='0.5') # 맵을 퍼블리시 하는 주기
    rviz_config_dir = os.path.join(get_package_share_directory('mapping'), 'rviz', 'slam.rviz')


    return LaunchDescription([
        DeclareLaunchArgument(
            'open_rviz',
            default_value='false',
            description='open rviz'),

        DeclareLaunchArgument(
            'cartographer_config_dir',
            default_value=cartographer_config_dir,
            description='Full path to config file to load'),
        DeclareLaunchArgument(
            'configuration_basename',
            default_value=configuration_basename,
            description='Name of lua file for cartographer'),
        DeclareLaunchArgument(
            'use_sim_time',
            default_value='false',
            description='Use simulation (Gazebo) clock if true'),
        DeclareLaunchArgument(
            'resolution',
            default_value=resolution,
            description='Resolution of a grid cell in the published occupancy grid'),
        DeclareLaunchArgument(
            'publish_period_sec',
            default_value=publish_period_sec,
            description='OccupancyGrid publishing period'),


        Node(
            package='cartographer_ros',
            executable='cartographer_node',
            name='cartographer_node',
            output='screen',
            parameters=[{'use_sim_time': use_sim_time}],
            arguments=[
                '-configuration_directory', cartographer_config_dir,
                '-configuration_basename', configuration_basename]),

        Node(
            package='cartographer_ros',
           executable='cartographer_occupancy_grid_node',
            name='occupancy_grid_node',
            output='screen',
            parameters=[{'use_sim_time': use_sim_time}],
            arguments=['-resolution', resolution, '-publish_period_sec', publish_period_sec]),

        Node(
            package='rviz2',
            executable='rviz2',
            name='rviz2',
            arguments=['-d', rviz_config_dir],
            parameters=[{'use_sim_time': use_sim_time}],
            condition=IfCondition(LaunchConfiguration("open_rviz")),
            output='screen'),
    ])
