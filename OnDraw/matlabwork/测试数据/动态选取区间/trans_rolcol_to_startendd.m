function [selected_section] = trans_rolcol_to_startendd(rol,col)
    start = rol-6;
    endd = start + col-1;
    selected_section = [start,endd];