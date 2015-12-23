function [result] = mean_oriacc(orient)
result = average_orient(orient,length(orient));

function [sum] = average_orient(array,count)
    sum = 0;
    count_left = 0;
    for index = 1:1:count
        sum = sum + array(index);
    end
    %弥补丢失的度数 比如 一个是1度一个是359度 正确的是0 但是上面计算下来是180
    %如果包含0方向，返回在0到90度之间的个数
    count_left = iscontainzero(array,count);
    sum = sum + count_left*360;
    sum = sum / count;
    sum = mod(sum , 360);

%方向范围中是否包含0度 返回在0到90度范围内的方向个数
function [count_left] = iscontainzero(array,count)
    %359到270为右
    count_right = 0;
    %0到90为左
    count_left = 0;
    for index = 1:1:count
        if array(index) >= 0 & array(index) < 90
            count_left = count_left+1;
        elseif(array(index) >= 270 && array(index) < 360)
            count_right = count_right+1;
        end
    end
    if count_left ~= 0 & count_right ~= 0
        count_left;
    else
        count_left = 0;
    end