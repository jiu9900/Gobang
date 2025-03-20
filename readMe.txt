同台设备：board.java 中 line30 设为 socket=new Socket("localhost",Server.PORT);
不同设备同一局域网：board.java 中 line30 设为 socket=new Socket("ip",Server.PORT);  ip 为所连局域网的 ip，可用 ipconfig 查看。

实时对弈：先运行 Server，再运行两次 Wuziqi（同台设备）。或者先运行 Server，然后在两台不同设备上运行 Wuziqi。


upd on 2024.12.26
增加缩放功能
判断平局（判断结束局面状态现在由 getStatus 方法执行）
在每局结束后可选择储存棋谱
增加了 Configuration.java，可以修改参数。WinCount=5 表示是五子棋，可以修改为 x 子棋。Bsize 是棋盘大小，默认为 15×15。Csize 为初始棋盘格边长。