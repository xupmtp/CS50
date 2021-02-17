#include<cs50.h>
#include<stdio.h>
#include <stdlib.h>
#include<string.h>
#include <ctype.h>

int main(int argc, string argv[])
{
    if (argc != 2)
    {
        printf("Usage: ./caesar key\n");
        return 1;
    }
    for (int i = 0; i < strlen(argv[1]); i++)
    {
        if (isdigit(argv[1][i]) == 0)
        {
            printf("Usage: ./caesar key\n");
            return 1;
        }
    }
    int k = atol(argv[1]);


    string input = get_string("plaintext: ");
    for (int i = 0, n = strlen(input); i < n; i++)
    {
        if (input[i] >= 'a' && input[i] <= 'z')
        {
            input[i] = ((input[i] - 'a' + k) % 26) + 'a';
        }
        else if (input[i] >= 'A' && input[i] <= 'Z')
        {
            input[i] = ((input[i] - 'A' + k) % 26) + 'A';
        }
    }

    printf("ciphertext: %s\n", input);
    return 0;
}