#include <cs50.h>
#include <stdio.h>
#include <string.h>

// Max number of candidates
#define MAX 9

// preferences[i][j] is number of voters who prefer i over j
int preferences[MAX][MAX];

// locked[i][j] means i is locked in over j
bool locked[MAX][MAX];

// Each pair has a winner, loser
typedef struct
{
    int winner;
    int loser;
}
pair;

// Array of candidates
string candidates[MAX];
pair pairs[MAX * (MAX - 1) / 2];

int pair_count;
int candidate_count;

// Function prototypes
bool vote(int rank, string name, int ranks[]);
void record_preferences(int ranks[]);
void add_pairs(void);
void sort_pairs(void);
void lock_pairs(void);
void print_winner(void);
void swap(int i, int j);
bool check_cycle(int j, int k);

int main(int argc, string argv[])
{
    // Check for invalid usage
    if (argc < 2)
    {
        printf("Usage: tideman [candidate ...]\n");
        return 1;
    }

    // Populate array of candidates
    candidate_count = argc - 1;
    if (candidate_count > MAX)
    {
        printf("Maximum number of candidates is %i\n", MAX);
        return 2;
    }
    for (int i = 0; i < candidate_count; i++)
    {
        candidates[i] = argv[i + 1];
    }

    // Clear graph of locked in pairs
    for (int i = 0; i < candidate_count; i++)
    {
        for (int j = 0; j < candidate_count; j++)
        {
            locked[i][j] = false;
        }
    }

    pair_count = 0;
    int voter_count = get_int("Number of voters: ");

    // Query for votes
    for (int i = 0; i < voter_count; i++)
    {
        // ranks[i] is voter's ith preference
        int ranks[candidate_count];

        // Query for each rank
        for (int j = 0; j < candidate_count; j++)
        {
            string name = get_string("Rank %i: ", j + 1);

            if (!vote(j, name, ranks))
            {
                printf("Invalid vote.\n");
                return 3;
            }
        }

        record_preferences(ranks);

        printf("\n");
    }

    add_pairs();
    sort_pairs();
    lock_pairs();
    print_winner();
    return 0;
}

// Update ranks given a new vote
bool vote(int rank, string name, int ranks[])
{
    // TODO
    for(int i = 0; i < candidate_count; i++)
    {
        if(strcmp(name, candidates[i]) == 0)
        {
            ranks[rank] = i;
            return true;
        }
    }
    return false;
}

// Update preferences given one voter's ranks
void record_preferences(int ranks[])
{
    // TODO
    for(int i = 0;i < candidate_count; i++)
    {
        for(int j = i + 1; j < candidate_count; j++)
        {
            preferences[ranks[i]][ranks[j]]++;
        }
    }
    return;
}

// Record pairs of candidates where one is preferred over the other
//將勝負加入pairs,更新pair_count(還有==情形,pair_count不能丟if外面)
void add_pairs(void)
{
    // TODO
    for(int i = 0;i < candidate_count; i++)
    {
         for(int j = i + 1; j < candidate_count; j++)
        {
            if(preferences[i][j] > preferences[j][i])
            {
                pairs[pair_count].winner = i;
                pairs[pair_count].loser = j;
                pair_count++;
            }
            else if(preferences[i][j] < preferences[j][i])
            {
                pairs[pair_count].winner = j;
                pairs[pair_count].loser = i;
                pair_count++;
            }
        }
    }
}

// Sort pairs in decreasing order by strength of victory
//氣泡排序
void sort_pairs(void)
{
    // TODO
    bool flag = true;
    for(int i = 0; i < pair_count; i++)
    {
        for(int j = i + 1; j < pair_count; j++)
        {
            if(preferences[pairs[i].winner][pairs[i].loser] < preferences[pairs[j].winner][pairs[j].loser])
            {
                swap(i, j);
                flag = false;
            }
        }
        if(flag)
        {
            break;
        }
    }
    return;
}

//change pairs
void swap(int i, int j)
{
    pair temp = pairs[i];
    pairs[i] = pairs[j];
    pairs[j] = temp;
}

// Lock pairs into the candidate graph in order, without creating cycles
//檢查是否有循環
void lock_pairs(void)
{
    // TODO
    for(int i = 0; i < pair_count; i++)
    {
        int w = pairs[i].winner;
        int l = pairs[i].loser;
        //如果循環則不建立箭頭(false),反之則建立(true)
        locked[w][l] = !check_cycle(w, l);
    }
    return;
}

bool check_cycle(int j, int k)
{
    //基本判斷
    if(locked[k][j])
    {
        return true;
    }
    for(int i = 0; i < candidate_count; i++)
    {
        //如果贏家有人贏過他
        if(locked[i][j])
        {
            if(check_cycle(i, k))
            {
                return true;
            }
        }
    }
    return false;
}

// Print the winner of the election
//需考慮平局時情況(互相false)所以自己沒被任何人贏過時就算勝利
void print_winner(void)
{
    // TODO
    for(int i = 0;i < candidate_count; i++)
    {
        int win = 0;
        for(int j = 0; j < candidate_count; j++)
        {
            if(!locked[j][i])
            {
                win++;
            }
        }
        if(win == candidate_count)
        {
            printf("%s\n", candidates[i]);
        }
    }
    return;
}

