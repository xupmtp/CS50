#include <stdio.h>
#include <math.h>

typedef struct
{
    int n;
}
node;

int main()
{
    node s;
    s->n = 5;
    printf("%i\n", s.n);
}