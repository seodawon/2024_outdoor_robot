from setuptools import find_packages, setup
import os
from glob import glob

package_name = 'tf_broadcast'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
        (os.path.join('share', package_name), glob('launch/*.launch.py'))
    
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='sh',
    maintainer_email='sh@todo.todo',
    description='TODO: Package description',
    license='TODO: License declaration',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            'tf_transform = tf_broadcast.tf_transform:main',
            'odom_node= tf_broadcast.odom_node:main',
            'clock_node= tf_broadcast.clock_node:main',
        ],
    },
)
