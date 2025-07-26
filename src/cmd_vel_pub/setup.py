from setuptools import find_packages, setup

package_name = 'cmd_vel_pub'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='seunghun',
    maintainer_email='seunghun@todo.todo',
    description='TODO: Package description',
    license='TODO: License declaration',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            'initial_pose_publisher = cmd_vel_pub.initialpose:main',
            'goal_publisher = cmd_vel_pub.goal_pose:main',
            'simple_publisher = cmd_vel_pub.cmd_vel:main',
            'gps_publisher = cmd_vel_pub.gps_publisher:main',
            'gps_receiver = cmd_vel_pub.gps_receiver:main'
        ],
    },
)
