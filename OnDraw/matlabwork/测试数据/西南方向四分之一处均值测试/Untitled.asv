tempdata=importdata('data_acc.txt');
plot(tempdata)
axis([100 200 -12 2])
grid on

x = (tempdata(220,1)+tempdata(219,1)+tempdata(218,1))/3
y = (tempdata(220,2)+tempdata(219,2)+tempdata(218,2))/3

x = (tempdata(730,1)+tempdata(729,1))/2
y = (tempdata(730,2)+tempdata(729,2))/2

x = tempdata(729,1)
y = tempdata(729,2)

v1 = y/sqrt(x*x+y*y)
v2 = asin(v1)

%x´óÓÚ0
v3 = (pi+v2)*180/pi

tempdata=importdata('data_angel12.txt');
mean(tempdata(1,:))
mean(tempdata(2,:))