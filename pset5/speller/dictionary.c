// Implements a dictionary's functionality

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <strings.h>

#include "dictionary.h"

// Represents a node in a hash table
typedef struct node
{
    char word[LENGTH + 1];
    struct node *next;
}
node;

// Number of buckets in hash table
const unsigned int N = 26;

// Hash table
node *table[N];
//word count
int count = 0;

// Returns true if word is in dictionary else false
bool check(const char *word)
{
    // TODO
    int h = hash(word);
    node *tmp = table[h];
    if (tmp == NULL)
    {
        return false;
    }
    while (tmp != NULL)
    {
        if (strcasecmp(tmp->word, word) == 0)
        {
            return true;
        }
        tmp = tmp->next;
    }
    return false;
}

// Hashes word to a number
unsigned int hash(const char *word)
{
    // TODO
    return tolower(word[0]) - 97;
}

// Loads dictionary into memory, returning true if successful else false
bool load(const char *dictionary)
{
    // TODO
    FILE *file = fopen(dictionary, "r");
    if (!file)
    {
        return false;
    }
    char word[LENGTH + 1];
    while (fscanf(file, "%s", word) != EOF)
    {
        node *n = malloc(sizeof(node));
        strcpy(n->word, word);
        n->next = NULL;
        int first = tolower(word[0]) - 97;
        n->next = table[first];
        table[first] = n;
        // free(n);
        count++;
    }
    fclose(file);
    return true;
}

// Returns number of words in dictionary if loaded else 0 if not yet loaded
unsigned int size(void)
{
    // TODO
    return count;
}

// Unloads dictionary from memory, returning true if successful else false
bool unload(void)
{
    // TODO
    for (int i = 0; i < N; i++)
    {
        if (table[i] == NULL)
        {
            continue;
        }
        node *list = table[i];
        while (list != NULL)
        {
            node *tmp = list->next;
            free(list);
            list = tmp;
        }
    }
    return true;
}
