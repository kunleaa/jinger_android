function openpicture(path,fold,selected_section)
    [rol,col] = size(selected_section);
    for i = 1 : rol
        open_picture(path,fold,get_picture_name(selected_section(i,2)-selected_section(i,1), selected_section(i,1), selected_section(i,2)));
    end
