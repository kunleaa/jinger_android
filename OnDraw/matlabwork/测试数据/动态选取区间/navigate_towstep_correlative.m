function [position_x, position_y, ori] = navigate_towstep_correlative(orient, acc_xyz, mmindex)
position_x(1) = 0;
position_y(1) = 0;

ori(2) = mean_oriacc([orient(1);orient(2)]);
ori(1) = ori(2);

angle_sin(2) = sin(ori(2)/180*pi);
angle_cos(2) = cos(ori(2)/180*pi);

angle_sin(1) = angle_sin(2);
angle_cos(1) = angle_cos(2);

distance(1) = calcudistan_Weinberg(-acc_xyz(mmindex(1,2),3),-acc_xyz(mmindex(1,1),3));
distance(2) = calcudistan_Weinberg(-acc_xyz(mmindex(2,2),3),-acc_xyz(mmindex(2,1),3));

position_x(2) = position_x(1) + angle_sin(1) * distance(1);
position_y(2) = position_y(1) + angle_cos(1) * distance(1);

position_x(3) = position_x(2) + angle_sin(2) * distance(2);
position_y(3) = position_y(2) + angle_cos(2) * distance(2);

for i = 3:length(orient)
    ori(i) = mean_oriacc([orient(i-1);orient(i)]);
    angle_sin(i) = sin(ori(i)/180*pi);
    angle_cos(i) = cos(ori(i)/180*pi);

    distance(i) = calcudistan_Weinberg(-acc_xyz(mmindex(i,2),3),-acc_xyz(mmindex(i,1),3));
    position_x(i+1) = position_x(i) + angle_sin(i) * distance(i);
    position_y(i+1) = position_y(i) + angle_cos(i) * distance(i);
end

ori = ori';