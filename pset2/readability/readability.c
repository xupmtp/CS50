#include <cs50.h>
#include <stdio.h>
#include <ctype.h>
#include <math.h>
#include <string.h>

int get_index();

void print_grade(int index);

float letters = 0.0;
float words = 1.0;
float sentences = 0.0;

int main(void)
{
    string text = get_string("Text: ");

    for(int i = 0; i < strlen(text); i++)
    {
        letters += isalpha(text[i]) != 0 ? 1.0 : 0.0;
        words += isblank(text[i]) != 0 ? 1.0 : 0.0;
        sentences += '!' == text[i] || '.' == text[i] || '?' == text[i] ? 1.0 : 0.0;
    }

    print_grade(get_index());

}

int get_index()
{
    float word_by_letters = (letters / words) * 100.0;
    float sentence_by_words = (sentences / words) * 100.0;
    float index = (0.0588 * word_by_letters) - (0.296 * sentence_by_words) - 15.8;
    return roundf(index);
}

void print_grade(int index)
{
    if(index < 1)
    {
        printf("%s\n", "Before Grade 1");
    }
    else if(index >= 16)
    {
        printf("%s\n", "Grade 16+");
    }
    else
    {
        printf("%s %i\n", "Grade", index);
    }
}