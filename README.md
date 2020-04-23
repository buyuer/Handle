# Handle
Android 虚拟手柄，通过蓝牙通信，可用于机器人、飞行棋控制和调试。

典型连接方式是，机器人上安装蓝牙串口模块，通过手机蓝牙连接模块，进行通信。

### 默认数据格式
一帧收据一个字节

0-60    61个值 为指令

61-125  65个值 左摇杆x轴 零点值93

126-190 65个值 右摇杆y轴 零点值158

191-155 65个值 右摇杆x轴 零点值223

可修改HandleActivity.java中callback回调的代码，自定义数据格式。

