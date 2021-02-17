#include<cs50.h>
#include<stdio.h>
#include <stdlib.h>
#include<string.h>
#include <ctype.h>

int main(int argc, string argv[])
{
    bool check[26] = {false};
    int letters[26];
    string salt = argv[1];

    if (argc != 2)
    {
        printf("Usage: ./substitution key\n");
        return 1;
    }
    if (strlen(salt) != 26)
    {
        printf("Key must contain 26 characters.\n");
        return 1;
    }
    for (int i = 0; i < strlen(salt); i++)
    {
        char c = toupper(salt[i]);
        if (isalpha(c) == 0 || check[c - 'A'])
        {
            printf("Usage: ./substitution key\n");
            return 1;
        }
        letters[i] = c - 'A';
        check[c - 'A'] = true;
    }

    string input = get_string("plaintext: ");

    for (int i = 0, n = strlen(input); i < n; i++)
    {
        if (input[i] >= 'a' && input[i] <= 'z')
        {
            input[i] = letters[input[i] - 'a'] + 'a';
        }
        else if (input[i] >= 'A' && input[i] <= 'Z')
        {
            input[i] = letters[input[i] - 'A'] + 'A';
        }
    }

    printf("ciphertext: %s\n", input);
    return 0;
}