%最小二乘圆拟合程序
 function [R,A,B]=circ(x,y,N) 
  x1 = 0;           %给定初值
  x2 = 0; 
  x3 = 0; 
  y1 = 0; 
  y2 = 0;
  y3 = 0;
  x1y1 = 0;
  x1y2 = 0;
  x2y1 = 0; 
  for i = 1 : N            %公式计算
      x1 = x1 + x(i);     
      x2 = x2 + x(i)*x(i);   
      x3 = x3 + x(i)*x(i)*x(i);   
      y1 = y1 + y(i);
      y2 = y2 + y(i)*y(i);   
      y3 = y3 + y(i)*y(i)*y(i); 
      x1y1 = x1y1 + x(i)*y(i);  
      x1y2 = x1y2 + x(i)*y(i)*y(i);   
      x2y1 = x2y1 + x(i)*x(i)*y(i);
  end 
  C = N * x2 - x1 * x1;
  D = N * x1y1 - x1 * y1; 
  E = N * x3 + N * x1y2 - (x2 + y2) * x1; 
  G = N * y2 - y1 * y1;  
  H = N * x2y1 + N * y3 - (x2 + y2) * y1;
  a = (H * D - E * G)/(C * G - D * D);
  b = (H * C - E * D)/(D * D - G * C); 
  c = -(a * x1 + b * y1 + x2 + y2)/N;
  A = a/(-2);          %y坐标 
  B = b/(-2);          %x坐标
R = sqrt(a * a + b * b - 4 * c)/2; 
  hold on; 
alpha=0:pi/20:2*pi;%角度[0,2*pi] 
y=A+R*cos(alpha);
x=B+R*sin(alpha);
plot(B,A,'b-');     %标注圆心
hold on
plot(x,y,'r');      %圆显示 