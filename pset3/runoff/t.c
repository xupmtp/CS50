#include <cs50.h>
#include <stdio.h>

void recursion(int i);

void main(void)
{
	int height = get_int("height: ");
}

void recursion(int i)
{
	if(i == 0)
	{
	  return;
	}
	recursion(i-1);

	for(int j = 0; j <= i; j++)
	{
	  printf("%s", "#");
	}
	printf("\n");
}