function [result] = mean_oriacc(orient)
result = average_orient(orient,length(orient));

function [sum] = average_orient(array,count)
    sum = 0;
    count_left = 0;
    for index = 1:1:count
        sum = sum + array(index);
    end
    %�ֲ���ʧ�Ķ��� ���� һ����1��һ����359�� ��ȷ����0 �����������������180
    %�������0���򣬷�����0��90��֮��ĸ���
    count_left = iscontainzero(array,count);
    sum = sum + count_left*360;
    sum = sum / count;
    sum = mod(sum , 360);

%����Χ���Ƿ����0�� ������0��90�ȷ�Χ�ڵķ������
function [count_left] = iscontainzero(array,count)
    %359��270Ϊ��
    count_right = 0;
    %0��90Ϊ��
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