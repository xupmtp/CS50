#include "helpers.h"
#include <stdio.h>
#include <math.h>

RGBTRIPLE getResult(int i, int j, int check[4], int width, RGBTRIPLE image[][width]);

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

// Detect edges
void edges(int height, int width, RGBTRIPLE image[height][width])
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

            res[i][j] = getResult(i, j, check, width, image);
        }
    }

    for (int i = 0; i < height; i++)
    {
        for (int j = 0; j < width; j++)
        {
            image[i][j] = res[i][j];
        }
    }
    printf("image already convert to edges\n");
    return;
}

RGBTRIPLE getResult(int i, int j, int check[4], int width, RGBTRIPLE image[][width])
{
    //存r, g, b
    int sumx[3] = {0, 0, 0};
    int sumy[3] = {0, 0, 0};
    RGBTRIPLE res;

    int gx[3][3] = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int gy[3][3] = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    for (int k = i - check[2], x = 1 - check[2]; k <= i + check[3]; k++, x++)
    {
        for (int n = j - check[1], y = 1 - check[1]; n <= j + check[0]; n++, y++)
        {
            sumx[0] += image[k][n].rgbtRed * gx[x][y];
            sumx[1] += image[k][n].rgbtGreen * gx[x][y];
            sumx[2] += image[k][n].rgbtBlue * gx[x][y];

            sumy[0] += image[k][n].rgbtRed * gy[x][y];
            sumy[1] += image[k][n].rgbtGreen * gy[x][y];
            sumy[2] += image[k][n].rgbtBlue * gy[x][y];
        }
    }
    int sqr = round(sqrt(sumx[0] * sumx[0] + sumy[0] * sumy[0]));
    int sqg = round(sqrt(sumx[1] * sumx[1] + sumy[1] * sumy[1]));
    int sqb = round(sqrt(sumx[2] * sumx[2] + sumy[2] * sumy[2]));

    res.rgbtRed = sqr > 255 ? 255 : sqr;
    res.rgbtGreen = sqg > 255 ? 255 : sqg;
    res.rgbtBlue = sqb > 255 ? 255 : sqb;

    return res;
}

