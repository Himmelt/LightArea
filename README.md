# 点亮区域 

### 简介
该Mod可以在服务端创建特殊亮度的区域，当玩家进入该区域时，其客户端会切换对应亮度（Gamma值），
玩家离开区域时恢复原亮度。

通过提高Gamma值，可以不用添加发光方块而达到提高建筑内部亮度的效果，同时能完美地消除建筑内部极其丑陋的阴影。

如果将Gamma值设置得较低（小于1）还可以实现将指定区域的夜色变得更黑暗的效果。

### 特性
1. 手持选区工具(默认木斧)左键选择起点，右键选择终点。
2. 在安装有WE-CUI的客户端，可以像WE一样显示选区边界。
3. 单文件支持 1.7.10 及以后版本。

### 指令
```
/light pos1             设置玩家当前位置为选区起点
/light pos2             设置玩家当前位置为选区终点
/light create [light]   根据选区创建区域，可选参数[light]为亮度级别[-15.0 - 15.0]
/light delete           删除玩家当前所在的区域
/light info             显示玩家当前所在区域的信息，如果客户端安装有WE-CUI，则会显示范围
/light level [light]    查看/设置当前区域的亮度
/light tool             手持为空，查看选区工具；手持非空，设置选区工具为当前手持物
```
 
# LightArea

### Description 
This mod can create areas with specific light.
When players move into these areas, their clients will change the light(gamma) to the specific light.
When they move out the area, the client will restore the original light.

By increase the gamma value, we can light the buildings' inside without luminous block 
and erase all the dark corners of the buildings.

If we set the gamma lower than 1.0, we can make the area darker.

### Features
1. Hold the select tool(default WoodenAxe), left click to set the start point, 
right click to set the end point.
2. The client with WE-CUI mod can show the selected area's border.
3. Single file supports 1.7.10 version and higher.

### Commands
```
/light pos1             set player's pos as area start point
/light pos2             set player's pos as area end point
/light create [light]   create area by current selected area, the optional arg is light (range: -15.0 - 15.0)
/light delete           delete the area at player's pos
/light info             show the info of the area at player's pos
/light level [light]    get/set the current area's light
/light tool             hand empty, show select tool; hand item,set select tool to the item in hand
```
