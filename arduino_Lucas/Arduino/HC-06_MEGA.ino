/* 
Referência em bauds(velocidade) para configurar o arduino
1----1200bps
2----2400bps
3----4800bps
4----9600bps
5----19200bps
6----38400bps
7----57600bps
8----115200bps
9----230400bps
A----460800bps
B----921600bps
C----1382400bps
*/

String command = "";  // resposta do bluetooth HC-06
int portSpeak(9);     //porta ligada no speaker
String voice = "";
int led1 = 2; //Conecta LED 1 no Pin #2
int led2 = 3; //Conecta LED 2 no Pin #3
int led3 = 4; //Conecta LED 4 no Pin #4
int fan1 = 5; //Conecta Fan no Pin #5

//melodia do MARIO THEME
int melodia[] = {660,660,660,510,660,770,380,510,380,320,440,480,450,430,380,660,760,860,700,760,660,520,580,480,510,380,320,440,480,450,430,380,660,760,860,700,760,660,520,580,480,500,760,720,680,620,650,380,430,500,430,500,570,500,760,720,680,620,650,1020,1020,1020,380,500,760,720,680,620,650,380,430,500,430,500,570,585,550,500,380,500,500,500,500,760,720,680,620,650,380,430,500,430,500,570,500,760,720,680,620,650,1020,1020,1020,380,500,760,720,680,620,650,380,430,500,430,500,570,585,550,500,380,500,500,500,500,500,500,500,580,660,500,430,380,500,500,500,500,580,660,870,760,500,500,500,500,580,660,500,430,380,660,660,660,510,660,770,380};

//duraçao de cada nota
int duracaodasnotas[] = {100,100,100,100,100,100,100,100,100,100,100,80,100,100,100,80,50,100,80,50,80,80,80,80,100,100,100,100,80,100,100,100,80,50,100,80,50,80,80,80,80,100,100,100,100,150,150,100,100,100,100,100,100,100,100,100,100,150,200,80,80,80,100,100,100,100,100,150,150,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,150,150,100,100,100,100,100,100,100,100,100,100,150,200,80,80,80,100,100,100,100,100,150,150,100,100,100,100,100,100,100,100,100,100,100,100,100,60,80,60,80,80,80,80,80,80,60,80,60,80,80,80,80,80,60,80,60,80,80,80,80,80,80,100,100,100,100,100,100,100};

void playMario() {
  //for para tocar as 156 notas começando no 0 ate 156 ++ incrementado
  for (int nota = 0; nota < 156; nota++) {
      int duracaodanota = duracaodasnotas[nota];
      tone(portSpeak, melodia[nota],duracaodanota);
      //pausa depois das notas
      int pausadepoisdasnotas[] ={150,300,300,100,300,550,575,450,400,500,300,330,150,300,200,200,150,300,150,350,300,150,150,500,450,400,500,300,330,150,300,200,200,150,300,150,350,300,150,150,500,300,100,150,150,300,300,150,150,300,150,100,220,300,100,150,150,300,300,300,150,300,300,300,100,150,150,300,300,150,150,300,150,100,420,450,420,360,300,300,150,300,300,100,150,150,300,300,150,150,300,150,100,220,300,100,150,150,300,300,300,150,300,300,300,100,150,150,300,300,150,150,300,150,100,420,450,420,360,300,300,150,300,150,300,350,150,350,150,300,150,600,150,300,350,150,150,550,325,600,150,300,350,150,350,150,300,150,600,150,300,300,100,300,550,575};
      delay(pausadepoisdasnotas[nota]);
    }
    noTone(portSpeak);
}

void offAllLeds(){
  digitalWrite(led1, LOW);
  digitalWrite(led2, LOW);
  digitalWrite(led3, LOW);
}

void onAllLeds(){
  digitalWrite(led1, HIGH);
  digitalWrite(led2, HIGH);
  digitalWrite(led3, HIGH);
}

void setup() {
  Serial.begin(9600);       //monitor
  Serial1.begin(9600);      //bluetooth 
  pinMode(led1, OUTPUT);
  pinMode(led2, OUTPUT); 
  pinMode(led3, OUTPUT); 
  pinMode(fan1, OUTPUT); 

  Serial.println("AT");  
  Serial1.print("AT");                  //PING
  if (Serial1.available()) {
    while(Serial1.available()) {
    delay(3);
    char c = Serial1.read();
      command += c;    
    }
  }

  delay(2000);
  Serial.println(command);
  command = ""; // No repeats
  Serial.println("AT+NAMELucas"); 
  Serial1.print("AT+NAMELucas");        //Altera nome do bluetoth, a partir de agora ele se chamará Lucas
  if (Serial1.available()) {
    while(Serial1.available()) {
        delay(3);
      command += (char)Serial1.read();  
    }
  }

  delay(2000);
  Serial.println(command);
  command = ""; // No repeats
  Serial.println("AT+PIN1234");
  Serial1.print("AT+PIN1234");        //Altera senha do bluetooth, deixado senha padrão
  if (Serial1.available()) {
    while(Serial1.available()) {
        delay(3);
      command += (char)Serial1.read();  
    }
  }

  delay(2000);   
  Serial.println(command);
  command = ""; // No repeats
  Serial.println("AT+BAUD4");  
  Serial1.print("AT+BAUD4");          //Altera bauds (Velocidade da transmisão serial, verificar referência acima caso queira alterar)
  if (Serial1.available()) {
    while(Serial1.available()) {
      command += (char)Serial1.read();    
      delay(2000); 
      Serial.println(command);
    } 
  } 
}

void loop(){  
                    
  if (Serial1.available()) {
    while(Serial1.available()) { // Continua lendo caso haver mais para le na serial.
      delay(100);
       voice += (char)Serial1.read(); 
    }
    Serial.println(voice);  
    Serial1.print(voice);
  }
  
  if(voice.length() > 0) {
    Serial1.println(voice);
    if (voice == "ligar vermelho" || voice == "led 1 ligar"){
      digitalWrite(led1, HIGH);
    }
    else if (voice == "desligar vermelho" || voice == "led 1 desligar"){
      digitalWrite(led1, LOW);
    }
    else if (voice == "ligar azul" || voice == "led 2 ligar"){
      digitalWrite(led2, HIGH);
    }
    else if (voice == "desligar azul" || voice == "led 2 desligar"){
      digitalWrite(led2, LOW);
    }
    else if (voice == "ligar verde" || voice == "led 3 ligar"){
      digitalWrite(led3, HIGH);
    }
    else if (voice == "desligar verde" || voice == "led 3 desligar"){
      digitalWrite(led3, LOW);
    }
    else if (voice == "ligar todos os leds"){
      onAllLeds();
    }
    else if (voice == "desligar todos os leds"){
      offAllLeds();
    }
    else if (voice == "ventilador"){
      digitalWrite(fan1, HIGH);
    }
    else if(voice == "desligar ventilador"){
      digitalWrite(fan1, LOW);
    }
    else if(voice == "tocar mario"){
      playMario();
    }
    else if(voice == "para mario"){
      noTone(portSpeak);
    }
  }
  voice="";
  delay(100);
}


