#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[])
{
    char *arg1 = argv[1];
    FILE *file = fopen(arg1, "r");
    if (file == NULL)
    {
        printf("no file\n");
        return 1;
    }
    unsigned char bytes[512];
    char *filename = malloc(10 * sizeof(char));
    int n = 0;
    FILE *img;

    //若fread() return不到512代表檔案到結尾
    while (fread(bytes, 1, 512, file) == 512)
    {
        //判斷是JPG檔
        if (bytes[0] == 0xff && bytes[1] == 0xd8 && bytes[2] == 0xff && (bytes[3] & 0xf0) == 0xe0)
        {
            //將字串設給filename變數
            sprintf(filename, "%03i.jpg", n++);
            //打開新的檔案
            img = fopen(filename, "w");
        }
        //寫入byte給空白檔案直到JPG結束
        fwrite(bytes, 1, 512, img);
    }
    //close file 後開先關
    fclose(img);
    free(filename);
    fclose(file);
}
