#include "helpers.h"
#include <stdio.h>
#include <math.h>

// Convert image to grayscale
void grayscale(int height, int width, RGBTRIPLE image[height][width])
{
    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            int avg = round(((float)image[i][j].rgbtBlue + image[i][j].rgbtGreen + image[i][j].rgbtRed) / 3);
            image[i][j].rgbtBlue = avg;
            image[i][j].rgbtGreen = avg;
            image[i][j].rgbtRed = avg;
        }
    }
    printf("image already convert to grayscale\n");
    return;
}

// Convert image to sepia
void sepia(int height, int width, RGBTRIPLE image[height][width])
{
    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            int r = image[i][j].rgbtRed;
            int g = image[i][j].rgbtGreen;
            int b = image[i][j].rgbtBlue;
            int convertR = round(0.393 * r + 0.769 * g + 0.189 * b);
            int convertG = round(0.349 * r + 0.686 * g + 0.168 * b);
            int convertB = round(0.272 * r + 0.534 * g + 0.131 * b);
            image[i][j].rgbtRed = convertR > 255 ? 255 : convertR;
            image[i][j].rgbtGreen = convertG > 255 ? 255 : convertG;
            image[i][j].rgbtBlue = convertB > 255 ? 255 : convertB;
        }
    }
    printf("image already convert to sepia\n");
    return;
}

// Reflect image horizontally
void reflect(int height, int width, RGBTRIPLE image[height][width])
{
    for (int i = 0; i < height; i++)
    {
        for (int j = 0, n = width / 2; j < n; j++)
        {
            RGBTRIPLE temp = image[i][j];
            image[i][j] = image[i][width - j - 1];
            image[i][width - j - 1] = temp;
        }
    }
    printf("image already convert to reflect\n");
    return;
}

// Blur image
void blur(int height, int width, RGBTRIPLE image[height][width])
{
    RGBTRIPLE res[height][width];
    int check[4] = {1, 0, 0, 1};//右左上下
    for (int i = 0; i < height; i++)
    {
        check[2] = i == 0 ? 0 : 1;
        check[3] = i == height - 1 ? 0 : 1;
        for (int j = 0; j < width; j++)
        {
            check[0] = j == width - 1 ? 0 : 1;
            check[1] = j == 0 ? 0 : 1;
            int sumr = 0;
            int sumg = 0;
            int sumb = 0;
            int count = 0;

            for (int k = i - check[2]; k <= i + check[3]; k++)
            {
                for (int n = j - check[1]; n <= j + check[0]; n++)
                {
                    sumr += image[k][n].rgbtRed;
                    sumg += image[k][n].rgbtGreen;
                    sumb += image[k][n].rgbtBlue;
                    count++;
                }
            }
            res[i][j].rgbtRed = round((float)sumr / count);
            res[i][j].rgbtGreen = round((float)sumg / count);
            res[i][j].rgbtBlue = round((float)sumb / count);
        }
    }
    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            image[i][j] = res[i][j];
        }
    }
    printf("image already convert to blur\n");
    return;
}
