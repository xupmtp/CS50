# [Problem Set 3](https://cs50.harvard.edu/x/2020/psets/3/)
---
### 1. [Plurality](https://cs50.harvard.edu/x/2020/psets/3/plurality/#plurality)
##### 題目說明
選舉初級
1. 在命令列(執行時的參數)輸入候選人名稱
2. 輸入選民人數
3. 依次輸入要投給哪位候選人
4. 計算總票數最高的人並print,若遇到相同票數則print多人
##### Answer
```c
#include <cs50.h>
#include <stdio.h>
#include <string.h>

// Max number of candidates
#define MAX 9

// Candidates have name and vote count
typedef struct
{
	string name;
	int votes;
}
candidate;

// Array of candidates
candidate candidates[MAX];

// Number of candidates
//選票總數
int candidate_count;

// Function prototypes
bool vote(string name);
void print_winner(void);

/*
**題目:args參數為候選人,輸入名稱表示投給該候選人,總票數為候選人人數
**找出最多票數候選人
*/
int main(int argc, string argv[])
{
	// Check for invalid usage
	if (argc < 2)
	{
			printf("Usage: plurality [candidate ...]\n");
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
			candidates[i].name = argv[i + 1];
			candidates[i].votes = 0;
	}

	int voter_count = get_int("Number of voters: ");

	// Loop over all voters
	for (int i = 0; i < voter_count; i++)
	{
			string name = get_string("Vote: ");

			// Check for invalid vote
			if (!vote(name))
			{
					printf("Invalid vote.\n");
			}
	}

	// Display winner of election
	print_winner();
}

// Update vote totals given a new vote
//這裡是我們實做的
//找候選人 選票++
bool vote(string name)
{
	for (int i = 0; i < candidate_count; i++)
	{
			if(strcmp(name,candidates[i].name) == 0)
			{
					candidates[i].votes++;
					return true;
			}
	}
	return false;
}

// Print the winner (or winners) of the election
//這裡是我們實做的
void print_winner(void)
{
	//max存最高票,j為print的指針
	int max = -1,j = 0;
	//存答案
	string print[MAX];
	for (int i = 0; i < candidate_count; i++)
	{
		//有新的最高票從頭開始存print內容
		if(candidates[i].votes > max)
		{
				max = candidates[i].votes;
				j=0;
				print[j++]=candidates[i].name;
		}
		else if(candidates[i].votes == max)
		{
				print[j++] = candidates[i].name;
		}
	}
	//到j之前的人都是最高票
	for (int i = 0; i < j; i++)
	{
			printf("%s\n",print[i]);
	}
	return;
}
```
### 2-1. [Runoff](https://cs50.harvard.edu/x/2020/psets/3/runoff/#runoff)
##### 題目說明
選舉分流
1. 每個人對所有候選人做排名投票
2. 從投票中選出勝利(排名第一)次數最多且得票數過半的候選人
3. 若沒有人得票數過半,則將得票數最低的人去除
4. 將去除者的票重新分組,這些票將改為投給排名下一位(原本排第一就給排第二的)的候選人
5. 重複2,3,4直到選出得票數最多且總得票過半數的人

>註:若這些票的下一位候選人已被淘汰,則順序自動延遞到下下一位

##### Answer
此題目實作為下列函數部分,其餘為題目

	bool vote(int voter, int rank, string name);
	void tabulate(void);
	bool print_winner(void);
	int find_min(void);
	bool is_tie(int min);
	void eliminate(int min);
```c
#include <cs50.h>
#include <stdio.h>
#include <string.h>

// Max voters and candidates
#define MAX_VOTERS 100
#define MAX_CANDIDATES 9

// preferences[i][j] is jth preference for voter i
//儲存選民偏好,j為候選人(對應candidates順序),i為選民,一直行 為一個選民的投票結果
//EX:preferences[1][0]為2表示第二個選民把第一位候選人排在第三順位(從0開始)
int preferences[MAX_VOTERS][MAX_CANDIDATES];

// Candidates have name, vote count, eliminated status
typedef struct
{
    string name;
    int votes;
    bool eliminated;
}
candidate;

// Array of candidates
candidate candidates[MAX_CANDIDATES];

// Numbers of voters and candidates
int voter_count;
int candidate_count;

// Function prototypes
bool vote(int voter, int rank, string name);
void tabulate(void);
bool print_winner(void);
int find_min(void);
bool is_tie(int min);
void eliminate(int min);

int main(int argc, string argv[])
{
    // Check for invalid usage
    if (argc < 2)
    {
        printf("Usage: runoff [candidate ...]\n");
        return 1;
    }

    // Populate array of candidates
    candidate_count = argc - 1;
    if (candidate_count > MAX_CANDIDATES)
    {
        printf("Maximum number of candidates is %i\n", MAX_CANDIDATES);
        return 2;
    }
    for (int i = 0; i < candidate_count; i++)
    {
        candidates[i].name = argv[i + 1];
        candidates[i].votes = 0;
        candidates[i].eliminated = false;
    }

    voter_count = get_int("Number of voters: ");
    if (voter_count > MAX_VOTERS)
    {
        printf("Maximum number of voters is %i\n", MAX_VOTERS);
        return 3;
    }

    // Keep querying for votes
    for (int i = 0; i < voter_count; i++)
    {

        // Query for each rank
        for (int j = 0; j < candidate_count; j++)
        {
            string name = get_string("Rank %i: ", j + 1);

            // Record vote, unless it's invalid
            if (!vote(i, j, name))
            {
                printf("Invalid vote.\n");
                return 4;
            }
        }

        printf("\n");
    }

    // Keep holding runoffs until winner exists
    while (true)
    {
        // Calculate votes given remaining candidates
        tabulate();

        printf("tabulate\n");
        // Check if election has been won
        bool won = print_winner();
        if (won)
        {
            break;
        }
        printf("print_winner\n");
        // Eliminate last-place candidates
        int min = find_min();
        bool tie = is_tie(min);

        printf("find_min,is_tie\n");
        // If tie, everyone wins
        if (tie)
        {
            for (int i = 0; i < candidate_count; i++)
            {
                if (!candidates[i].eliminated)
                {
                    printf("%s\n", candidates[i].name);
                }
            }
            break;
        }

        // Eliminate anyone with minimum number of votes
        eliminate(min);

        // Reset vote counts back to zero
        for (int i = 0; i < candidate_count; i++)
        {
            candidates[i].votes = 0;
        }
    }
    return 0;
}

// Record preference if vote is valid
//更新preferences陣列層級
bool vote(int voter, int rank, string name)
{
    // TODO
    for(int i = 0; i < candidate_count; i++)
    {
        if(strcmp(candidates[i].name, name) == 0)
        {
            preferences[voter][i] = rank;
            return true;
        }
    }
    return false;
}

// Tabulate votes for non-eliminated candidates
// 將票投給
void tabulate(void)
{
    // TODO
    for(int i = 0; i < voter_count; i++)
    {
        int j = 0;
        while(candidates[preferences[i][j]].eliminated)
        {
            j++;
        }
        candidates[preferences[i][j]].votes++;
    }
    return;
}

// Print the winner of the election, if there is one
bool print_winner(void)
{
    for(int i = 0; i < candidate_count; i++)
    {
        if(candidates[i].votes > voter_count/2)
        {
            printf("%s\n", candidates[i].name);
            return true;
        }
    }
    return false;
}

// Return the minimum number of votes any remaining candidate has
int find_min(void)
{
    // TODO
    int min = 2147483647;
    for(int i = 0; i < candidate_count; i++)
    {
        if(candidates[i].votes < min && !candidates[i].eliminated)
        {
            min = candidates[i].votes;
        }
    }
    return min;
}

// Return true if the election is tied between all candidates, false otherwise
bool is_tie(int min)
{
     for(int i = 0; i < candidate_count; i++)
    {
        if(!candidates[i].eliminated && candidates[i].votes != min)
        {
            return false;
        }
    }
    return true;
}

// Eliminate the candidate (or candidiates) in last place
void eliminate(int min)
{
    // TODO
    for(int i = 0; i < MAX_CANDIDATES; i++)
    {
        if(candidates[i].votes == min && !candidates[i].eliminated)
        {
           candidates[i].eliminated = true;
        }
    }
    return;
}
```

### 2-2. [tideman](https://cs50.harvard.edu/x/2020/psets/3/tideman/)
選舉樹狀圖,此題較複雜詳細看影片說明
##### 題目說明
1. 同題目2,每個選民先進行排名投票
2. 若A在此張選票中排名比B高則記A贏一票,反之記B贏一票,若A贏的次數多於B則記A贏B
3. 依2.的方法算出每個人之間的關係,A贏B則畫一箭頭往B
4. 找出沒有箭頭指向的候選人即為優勝者
5. 當沒有一候選人沒被人贏過時執行6.
6. 將候選人關係依以下規則畫為[有向圖](https://zh.wikipedia.org/wiki/%E5%9B%BE_(%E6%95%B0%E5%AD%A6)#%E6%9C%89%E5%90%91%E5%9B%BE%E5%92%8C%E7%84%A1%E5%90%91%E5%9B%BE)
 	1. 依獲勝票數排序pairs
 	2. 由高票獲勝者開始畫圖
 	3. 若今天輸家b有指向其他候選人且不斷指向後最後會指到贏家a,則a不能指向b(代表b為高票獲勝者)
7. 最後可得到沒被贏過得獲選人(可能為複數)

##### Answer

實做部份為

	bool vote(int rank, string name, int ranks[]);
	void record_preferences(int ranks[]);
	void add_pairs(void);
	void sort_pairs(void);
	void lock_pairs(void);
	void print_winner(void);	
自己增加的函數有

	//交換位置
	void swap(int i, int j);
	//遞迴尋找是否會循環
	bool check_cycle(int j, int k);

```c
#include <cs50.h>
#include <stdio.h>
#include <string.h>

// Max number of candidates
#define MAX 9

// preferences[i][j] is number of voters who prefer i over j
int preferences[MAX][MAX];

// locked[i][j] means i is locked in over j
//此陣列儲存為有向圖,表示選舉人間勝負關係
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
//投票
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
//將選民排名結果更新到preferences
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
//氣泡排序,贏票越多排越前
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
//檢查是否有循環,此題關鍵部分
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
    //如果輸家贏過贏家則代表循環
    if(locked[k][j])
    {
        return true;
    }
    for(int i = 0; i < candidate_count; i++)
    {
        //如果贏家有人贏過他
        if(locked[i][j])
        {
						//往上找有沒有贏家的贏家被輸家贏過,有則代表循環
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
```