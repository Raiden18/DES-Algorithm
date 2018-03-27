package com.company;

public class Des {

    private long encodeText;

    public Des (long text, long key){
        //Начальная перестановка
        long openText = reshuffleBits(text, Tables.IP);

        //Разделям перемешанную последовательность бит на леву и правую часть
        long left = getSequence(openText, 32, 64); //Считаем справо налево

        long right = getSequence(openText, 0, 32); //Считаем справо налево

        //Расширяем правую последовательность
        //right = extendingE(right);

        //Расширенный ключ
        long key64 = extendingKey(key);

        long key56 = reshuffleBits(key64, Tables.G);
        //Разделяем ключ на левую и правую части
        long leftKey28 = getSequence(key56, 28, 56); //Считаем справо налево
        long rightKey28 = getSequence(key56, 0, 28); //Считаем справо налево

        byte[] pos = {0, 6, 12, 18, 24, 30, 36, 42, 48}; // Массив для деления E(R)xorK(i)

        //Реализация функции Фейстеля
        for (int i = 0; i < 16; i++){
            //Сдвигаем левую и правую части ключа
            rightKey28 = shiftBits(rightKey28, Tables.SHIFT[i]);
            leftKey28 = shiftBits(leftKey28, Tables.SHIFT[i]);
            //Соединяем части и перемешиваем согласно матрице
            long key56new = gatherBits(rightKey28, leftKey28);
            long key48 = reshuffleBits(key56new, Tables.H); //Ключ подготовлен

            //Вычисляем правую часть
            long rightExteding = extendingE(right);
            //Ксорим правую часть текста и ключ
            long f = rightExteding ^ key48;

            long temp = 0;

            //реализая функции f
            long temp_f = 0; // собираем f
            for (int j = 7; j >= 0; j--){
                temp = getSequence(f, pos[j], pos[j+1]);
                long bi = functionS(temp, j);
                temp_f = temp_f << 4;
                temp_f |= bi;
            }

            f = reshuffleBits(temp_f, Tables.P); //Функция f готова!

            long temp_right = right;
            right = left ^ f;
            left = temp_right;
        }

        //объединяем левую и правую части
        long result = 0;
        result |= left;
        result = result << 32L;
        result |=right;

        encodeText = reshuffleBits(result, Tables.IP_REVERS);
    }


    public long getEncodeMessage(){
        return encodeText;
    }

    /**
     * Реалицая функции S
     * @param bitsSequence - 6-битная последовательность
     * @param n - номер функции s
     * @return возвращает 4х битную последовательность
     */
    private long functionS(long bitsSequence, int n){
        byte a = 0;

        byte bit6 = (byte) getBit(bitsSequence, 5);
        a |= bit6;
        a = (byte)(a << 1);
        byte bit1 = (byte) getBit(bitsSequence, 0);
        a |= bit1;

        byte b = 0;
        for (int i = 4; i>=1; i--){
            byte biti = (byte) getBit(bitsSequence, i);
            b = (byte)(b << 1);
            b |=biti;
        }

        byte bb= Tables.S[n][a*16+b];

        return bb;
    }

    /**
     * Соединяет две 28-битных последовательностей бит в одну 56-битную
     * @param left
     * @param right
     * @return 56-битная последовательность
     */
    private long gatherBits(long left, long right){
        long bits64 = 0;
        bits64 |= left;
        bits64 = bits64 << 28;
        bits64 = bits64 | right;
        return bits64;
    }

    /**
     *  Полуение последовательности бит с позиции from по позицию to
     * @param bitsSequence
     * @param from
     * @param to
     * @return необходимая последовательность битов
     */
    private long getSequence(long bitsSequence, int from, int to){
        long sequence =  0;

        for (int i = from; i<to; i++){
            byte bit = (byte)getBit(bitsSequence, i);
            sequence = setBit(sequence, i, bit);
        }

        if (from>0){
            sequence = sequence >>> (long)from;
        }
        return sequence;
    }


    /**
     * Расширение ключа с 56 бит до 64 бит
     * @param key56
     * @return расширенный 64битный ключ
     */
    private long extendingKey(long key56) {
        long key64 = 0;
        byte count = 0; //переменная для подсчета количесва бит
        int bit8 = 0;

        for (long i = 55; i >= 0; i--) {
            //берем бит из 56-битного ключа
            byte bit = (byte)getBit(key56, i);
            if (bit == 1)
                count++;

            bit8 |= bit;
            bit8 = (bit8 << 1);

            //Собрали байт:
            if (i % 7 == 0){
                //Сдвигаем на один бит влево для вставки контрольного бита
                bit8 = (bit8 >>> 1);
                if (count % 2 !=0)
                    bit8 = (int)setBit(bit8, 7, 1);

                //Собираем биты вместе
                key64 = key64 | bit8;
                if (i!=0){
                    key64 = key64 << 8L;
                    bit8 = 0;
                    count = 0;
                }
            }
        }
        return key64;
    }

    /**
     * Функция расширения 32 битовой последовательности правой части ключа в 48 последовательность
     * @param rightSide32
     * @return возвращает 48 последовательность бит
     */
    private long extendingE(long rightSide32){
        long rightSide64 = 0;

        for (int i = 0; i < Tables.E.length; i++){
            long bit = getBit(rightSide32, Tables.E[i]-1);
            rightSide64 = setBit(rightSide64, i, bit);
        }

        return rightSide64;
    }


    /**
     * Начальная перестановка открытого текста
     * @param openText
     * @return возвращает перемешанную последовательность бит согласно матрице начальной перестановки IP
     */
    private long reshuffleBits(long openText, byte[] table){
        long newText = 0;

        for (int i = 0; i < table.length; i++) {
           long bit = getBit(openText, table[i] - 1);
           newText = setBit(newText, i, bit);
        }

        return newText;
    }

    /**
     * Получениие бита числа в указанной позиции
     * @param number
     * @param position
     * @return бит числа в указанной позиции
     */
    private long getBit (long number, long position){
        return (number >> position) & 1L;
    }

    /**
     * Устанавливка бита в указанную позицию
     * @param number
     * @param position
     * @return число с измененным битом в указанной позиции
     */
    private long setBit (long number, long position, long bit){
        //return number|(bit << position); //было
        return number ^ bit << position;

    }

    /**
     * Очищает бит в указанной позиции
     * @param number
     * @param position
     * @return последовательность бит с очищенными битами в указанной позиции
     */
    private long clearBit (long number, int position){
        number&= ~(1 << position);
        return number;

    }

    /**
     * Циклический сдвиг битов влево.
     * @param bitsSequence
     * @param offset
     * @return последовальнсоть битов, циклически сдвинутая влево
     */
    private long shiftBits(long bitsSequence, int offset){
        long tempBits = 0;
        bitsSequence = bitsSequence << offset;

        for (int i= 27+offset; i>27; i--){
            byte bit = (byte) getBit(bitsSequence, i);
            tempBits <<= 1L;
            tempBits |=bit;

            bitsSequence = clearBit(bitsSequence, i);
        }
        bitsSequence |= tempBits;

        return bitsSequence;
    }

    public static void main(String[] args) {
        //Спарав считаем бит
        long opT = 0b1010100111000110100101000010011010010000010100100001010100001100L;
        long ke = 0b10101010001101101001111111111010101101010100011111101010L;

        Des des = new Des(opT, ke);
        System.out.println(Long.toBinaryString(des.getEncodeMessage()));


    }
}
