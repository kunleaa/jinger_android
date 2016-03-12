function [position_x, position_y, ori] = navigate_towstep_independent(orient, acc_xyz, mmindex)
position_x(1) = 0;
position_y(1) = 0;
for i=2:2:length(orient)
    
    ori(i) = mean_oriacc([orient(i);orient(i-1)]);
    ori(i-1) = ori(i);
    
    angle_sin(i) = sin(ori(i)/180*pi);
    angle_cos(i) = cos(ori(i)/180*pi);
    
    distance(i-1) = calcudistan_Weinberg(-acc_xyz(mmindex(i-1,2),3),-acc_xyz(mmindex(i-1,1),3));
    distance(i) = calcudistan_Weinberg(-acc_xyz(mmindex(i,2),3),-acc_xyz(mmindex(i,1),3)) + distance(i-1);
    
    position_x(i+1) = position_x(i-1) + angle_sin(i) * distance(i);
    position_y(i+1) = position_y(i-1) + angle_cos(i) * distance(i);
    
    position_x(i) = position_x(i+1);
    position_y(i) = position_y(i+1);
end
%奇数个值时 补充最后一个值
ori(length(orient)) = ori(length(ori));
ori = ori';