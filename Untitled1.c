#include<stdio.h>
#include<stdlib.h>
#include<math.h>

class dolgozo{

protected:
    int azon;
    int oraber;
    int munkaora;

public:
    void dolgozok(int a, int o, int m);
    int hetiber(void);

};

void dolgozo::dolgozok(int a, int o, int m) {
    azon=a;
    oraber=o;
    munkaora=m;
}

double dolgozo::hetiber(void) {
    return oraber*munkaora;

}

class irodai: public dolgozo {
protected:
    int irodaszam;
public:
    void dolgozok(int a, int o, int m, int i);
};

void irodai::dolgozok(int a, int o, int m, int i) {
    irodaszam=i;
    azon=a;
    oraber=o;
    munkaora=m;

}

class vezeto: public dolgozo {
protected:
    int potlek;
public:
    void dolgozok(int a, int o, int m, int p);
    int hetiber(void);
};

void irodai::dolgozok(int a, int o, int m, int p) {
    potlek=p;
    azon=a;
    oraber=o;
    munkaora=m;

}

double dolgozo::hetiber(void) {
    return oraber*munkaora;

}

class irodavezeto: public irodai, public vezeto {
public:
    void dolgozok(int a, int o, int m, int p, int i);
};

void irodai::dolgozok(int a, int o, int m, int p, int i) {
    irodaszam=i;
    potlek=p;
    azon=a;
    oraber=o;
    munkaora=m;

}

int main(){
    class dolgozo elso;
    elso.dolgozok(1,500, 40);
    printf("A hetiber: %d\n", elso.hetiber());

    class irodai masodik;
    masodik.ertekadas(3,4);
    printf("A teherauto hatekonysaga: %.2f\n", masodik.hatekonysag());

    class vezeto harmadik;
    harmadik.ertekadas(1,2,3,4);
    printf("Soforauto hatekonysaga: %.2f\n", harmadik.hatekonysag());


    system("PAUSE");
}
