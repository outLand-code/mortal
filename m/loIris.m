%.人眼外虹膜定位
clear;close all;
I=imread('ip1.jpg')
 f = rgb2gray(I);
imhist(f);                       %  求图像的直方图                  
[F_Size_M F_Size_N] = size(f);   %  获取f的行和列 

T =84;     
for i = 1:F_Size_M
    for j = 1:F_Size_N
        if f(i,j) >= T
            f(i,j) = 255;
        else
            f(i,j) = 0;
        end
    end
end
imshow(f);
f = ~f;
se = strel('disk',3);
f1 = imclose(f,se);
se = strel('disk',3);
f2 = imopen(f1,se);
f3 = imfill(f2,'holes');
f4 = im2double(f3);
f4 = medfilt2(f3,[3 3]);
%  imshow(f4);

% 标记，查找，分离最大联通域

[x,num] = bwlabel(f4,4);               % 标记联通域 

% 标记每一个联通域的像素个数
for j = 1 : num
    [r,c]   = find(x == j);
    rc      = [r c];
    long(j) = size(rc,1);
end
% 查找最大联通域
max = 0;
m=0;
for i = 1 : num
    if long(i) > max
        max = long(i);
        m= i;
    end
end

% 分离最大联通域
[row column] = size(x);
for i = 1 : row
    for j = 1 : column
        if x(i,j) == m
           x(i,j) = 1;
        else
            x(i,j) = 0;
        end
    end
end
  figure,imshow(x);

% % 利用cann算子进行边缘检测，以得到单像素的边缘图像

 k = edge(x,'canny');  % 边缘检测
 [m,n] = find(k == 1); % 记录边缘的坐标
 figure,imshow(I);
D=[m,n];
row=length(D);%记录边缘点个数

[R,A,B]=circ(m,n,row);%调用函数，圆拟合的定位显示
