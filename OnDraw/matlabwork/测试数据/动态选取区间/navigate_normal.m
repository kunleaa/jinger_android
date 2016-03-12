function [position_x, position_y, orient] = navigate_normal(orient, acc_xyz, mmindex)

position_x(1) = 0;
position_y(1) = 0;
for i = 1:length(orient)
    angle_sin(i) = sin(orient(i)/180*pi);
    angle_cos(i) = cos(orient(i)/180*pi);

    distance(i) = calcudistan_Weinberg(-acc_xyz(mmindex(i,2),3),-acc_xyz(mmindex(i,1),3));
    position_x(i+1) = position_x(i) + angle_sin(i) * distance(i);
    position_y(i+1) = position_y(i) + angle_cos(i) * distance(i);
end