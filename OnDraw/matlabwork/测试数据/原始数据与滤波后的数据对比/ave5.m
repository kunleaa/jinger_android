tempdata=importdata('AbsoluteCoordinate����20��1.txt');

ave_x = [1:length(tempdata)-5];
for i = 5:length(tempdata)
    ave_x(i-4) = mean(tempdata(i-4:i,1));
end
ave_y = [1:length(tempdata)-5];
for i = 5:length(tempdata)
    ave_y(i-4) = mean(tempdata(i-4:i,2));
end

data = [ave_x;ave_y;tempdata(5:length(tempdata),3)';tempdata(5:length(tempdata),4)']';


plot(data)
axis([100 600 -20 5])
grid on