#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctime>

int cases, caseno;
int n, m, k, a[50][50], p;
bool sol[50];

int main() {
	freopen("d.in", "w", stdout);

	int cases = 10;

    srand(time(NULL));
	printf("%d\n", cases);
	while( cases-- ) {
		int n = rand() % 5 + 1;
		int k = rand() % 30 + 1;
		int m = rand() % 3 + 1;

		//if( cases == 5 || cases == 10 || cases == 15 ) n = k = m = 30;

		printf("%d %d %d\n", n, m, k);

		int p = rand() % (m + 1);

		if( cases == 15 ) p = 0;

		bool sol[50] = {0};
		for( int i = 0; i < p; i++ ) {
			int x = rand() % m + 1;
			sol[x] = true;
		}

		int W = rand() % n;
		int XX = rand() % 3;
		for( int i = 0; i < n; i++ ) {
			int flag[50] = {0};
			for( int j = 0; j < k; j++ ) {
				int x = rand() % m + 1;

				if( flag[x] == 0 ) {
					if( XX == 0 && W == i ) {
						if( sol[x] ) x *= -1;
					}
					else if( rand() % 2 ) {
						x *= -1;
					}
					flag[ abs(x) ] = x;

					a[i][j] = x;
				}
				else a[i][j] = flag[x];
			}

			if( XX == 0 && W == i ) continue;
			int x = rand() % k;
			int xx = rand() % m + 1;

			if( sol[xx] ) a[i][x] = xx;
			else a[i][x] = -xx;
		}

		for( int i = 0; i < n; i++ ) {
			for( int j = 0; j < k; j++ ) {
				if( j ) putchar(' ');
				printf("%+d", a[i][j]);
			}
			puts("");
		}


		p = 0;
		for( int i = 1; i <= m; i++ ) if( sol[i] ) p++;

		printf("%d", p);
		for( int i = 1; i <= m; i++ ) if( sol[i] ) printf(" %d", i);
		puts("");
	}
	return 0;
}

