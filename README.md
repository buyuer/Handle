# Handle
Android 虚拟手柄，通过蓝牙通信，可用于机器人、飞行棋控制和调试。

典型连接方式是，机器人上安装蓝牙串口模块，通过手机蓝牙连接模块，进行通信。

(注意：此app仅用于学习之用，一切责任由使用者自行承担)
### 默认数据格式
一帧收据一个字节

0-50    51个值 左摇杆y轴 零点值25

51-101  51个值 左摇杆x轴 零点值76

102-152 51个值 右摇杆y轴 零点值127

153-203 51个值 右摇杆x轴 零点值178

204-255 52个值 指令

可修改HandleActivity.java中callback回调的代码，自定义数据格式。

