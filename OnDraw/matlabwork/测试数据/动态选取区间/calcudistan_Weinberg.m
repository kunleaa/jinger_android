function [ori] = calcudistan_Weinberg(max_acc, min_acc)
    ori = (max_acc-min_acc)^(1/4);