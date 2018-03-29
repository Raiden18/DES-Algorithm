package com.company;

/** Класс служит для реализации алгоритма DES
 *  @author Karpukhin Pavel
 */
public class Des {
    /**
     * Матрица Начальной мерестановки
     */
    private static final byte[]  IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17,  9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7,
    };

    /**
     * Матрица обратной мерестановки
     */
    private static final byte[] IP_REVERS = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 61, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9,  49, 17, 57, 25,
    };

    /**
     * Таблица, определяющая функцию расширения.
     */
    private static final byte[] E = {
            32,  1,  2,  3,  4,  5,
            4,  5,  6,  7,  8,  9,
            8,  9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32,  1,
    };

    /**
     * Функции преобразования S1, S2, S3,..., S8
     */
    private static final byte[][] S = {{
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
    }, {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
    }, {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
    }, {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
    }, {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
    }, {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
    }, {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
    }, {
            13,  2,  8, 4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7,
            1, 15, 13, 8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2,
            7, 11,  4, 1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8,
            2,  1, 14, 7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11
    }};

    /**
     * Таблица, определяющая функцию перестановки
     */
    private static final byte[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    /**
     * Матрица первоначальной обработки ключа
     */
    private static final byte[] G = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63,55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12,  4
    };

    /**
     * Массив сдвигов для вычисления ключа.
     */
    private static final byte[] SHIFT = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    /**
     * Массив сдвигов для вычисления ключа для расшифрования.
     */
    private static final byte[] SHIFT_REV = {
            0, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    /**
     * Матрица заверщающей обработки ключа
     */
    private static final byte[] H = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    /** текст */
    private long text;

    /** Ключ */
    private long key;

    public Des (long text, long key){
        this.text = text;
        this.key = key;
    }

    /**
     * Возвращает декодированную алгоритмом DES 64-разрядную последовательность бит
     * @return деккодированная 64-разрядная последовательнось бит
     */
    public long getDecodeMessage(){
        //Начальная перестановка
        long openText = reshuffleBits(text, IP);

        //Разделям перемешанную последовательность бит на леву и правую часть
        long left = getSequence(openText, 32, 64); //Считаем справо налево
        long right = getSequence(openText, 0, 32); //Считаем справо налево

        //Удаление контрольных битов ключа и перемешивание его битов
        long key56 = reshuffleBits(key, G);

        //Разделяем ключ на левую и правую части
        long leftKey28 = getSequence(key56, 28, 56); //Считаем справо налево
        long rightKey28 = getSequence(key56, 0, 28); //Считаем справо налево

        byte[] pos = {0, 6, 12, 18, 24, 30, 36, 42, 48}; // Массив для деления E(R)xorK(i)
        //Реализация функции Фейстеля
        for (int i = 0; i<16; i++){

            //Сдвигаем левую и правую части ключа
            rightKey28 = rightShiftBits(rightKey28, SHIFT_REV[i]);
            leftKey28 = rightShiftBits(leftKey28, SHIFT_REV[i]);



            //Соединяем части и перемешиваем согласно матрице
            long key56new = gatherBits(leftKey28, rightKey28, 28);

            long key48 = reshuffleBits(key56new, H); //Ключ подготовлен

            //Вычисляем правую часть
            long leftExteding = reshuffleBits(left, E);

            //Ксорим правую часть текста и ключ
            long f = leftExteding ^ key48;
            /*XOR работает корректно*/

            long temp;

            //реализая функции f
            long temp_f = 0; // собираем f

            for (int j = 7; j >= 0; j--){
                temp = getSequence(f, pos[j], pos[j+1]);

                long bi = functionS(temp, j);

                temp_f = temp_f << 4L;
                temp_f |= bi;
            }

            f = reshuffleBits(temp_f, P); //Функция f готова!

            long temp_left = left;
            left = right ^ f;
            right = temp_left;
        }

        //объединяем левую и правую части
        long result = gatherBits(left, right, 32);

        return reshuffleBits(result, IP_REVERS);
    }

    /**
     * Возвращает кодированную алгоритмом DES 64-разрядную последовательность бит
     * @return кодированная 64-разрядная последовательнось бит
     */
    public long getEncodeMessage(){
        //Начальная перестановка
        long openText = reshuffleBits(text, IP);


        //Разделям перемешанную последовательность бит на леву и правую часть
        long left = getSequence(openText, 32, 64); //Считаем справо налево
        long right = getSequence(openText, 0, 32); //Считаем справо налево

         //Удаление контрольных битов ключа и перемешивание его битов
        long key56 = reshuffleBits(key, G);

        //Разделяем ключ на левую и правую части
        long leftKey28 = getSequence(key56, 28, 56); //Считаем справо налево
        long rightKey28 = getSequence(key56, 0, 28); //Считаем справо налево

        byte[] pos = {0, 6, 12, 18, 24, 30, 36, 42, 48}; // Массив для деления E(R)xorK(i)
        //Реализация функции Фейстеля
        for (int i = 0; i < 16; i++){
            //Сдвигаем левую и правую части ключа

            rightKey28 = leftShiftBits(rightKey28, SHIFT[i]);
            leftKey28 = leftShiftBits(leftKey28, SHIFT[i]);

            //Соединяем части и перемешиваем согласно матрице
            long key56new = gatherBits(leftKey28, rightKey28, 28);

            long key48 = reshuffleBits(key56new, H); //Ключ подготовлен

            //Вычисляем правую часть
            long rightExteding = reshuffleBits(right, E);

            //Ксорим правую часть текста и ключ
            long f = rightExteding ^ key48;
            /*XOR работает корректно*/

            long temp;

            //реализая функции f
            long temp_f = 0; // собираем f

            for (int j = 7; j >= 0; j--){
                temp = getSequence(f, pos[j], pos[j+1]);

                long bi = functionS(temp, j);

                temp_f = temp_f << 4L;
                temp_f |= bi;
            }

            f = reshuffleBits(temp_f, P); //Функция f готова!

            long temp_right = right;
            right = left ^ f;
            left = temp_right;
        }

        //объединяем левую и правую части
        long result = gatherBits(left, right, 32);

        return reshuffleBits(result, IP_REVERS);
    }

    /**
     * Реалицая функции S
     * @param bitsSequence - 6-разрядная последовательность бит
     * @param n - номер функции S
     * @return возвращает 4-разрядну последовательность бит
     */
    private long functionS(long bitsSequence, int n){
        long a = 0;

        long bit6 = getBit(bitsSequence, 5);
        a |= bit6;
        a = a << 1L;
        long bit1 =  getBit(bitsSequence, 0);
        a |= bit1;

        long b = 0;
        for (int i = 4; i>=1; i--){
            long biti =  getBit(bitsSequence, i);
            b = (b << 1L);
            b |=biti;
        }

        return S[n][(int)a*16+(int)b];
    }

    /**
     * Соединяет две 28-разрядных последовательностей бит в одну 56-разрядную
     * @param left  левая часть 56-разрядной последовательности бит
     * @param right правая часть 56-разрядной последовательности бит
     * @return 56-разрядная последовательность бит
     */
    private long gatherBits(long left, long right, long length){
        long bits64 = 0;
        bits64 |= left;
        bits64 <<= length;
        return bits64 | right;
    }

    /**
     *  Полуение последовательности бит с позиции from по позицию to (справа налево!)
     * @param bitsSequence - последоваетельеость бит
     * @param from - номер позиции с которой начинаем считывать биты
     * @param to - последний бит позиции
     * @return необходимая последовательность бит
     */
    private long getSequence(long bitsSequence, long from, long to){
        long sequence =  0;

        for (long i = from; i<to; i++){
            long bit = getBit(bitsSequence, i);
            sequence = setBit(sequence, i, bit);
        }

        if (from>0){
            sequence >>>= from;
        }

        return sequence;
    }


    /**
     * Расширение ключа с 56 бит до 64 бит
     * @param key56 - 56-разрядный ключ
     * @return расширенный 64-битный ключ
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
            bit8 <<= 1;

            //Собрали байт:
            if (i % 7 == 0){
                //Сдвигаем на один бит влево для вставки контрольного бита
                bit8 >>>= 1L;
                if (count % 2 !=0)
                    bit8 = (int)setBit(bit8, 7, 1);

                //Собираем биты вместе
                key64 <<= 8L;
                key64 |= bit8;

                bit8 = 0;
                count = 0;
            }
        }
        return key64;
    }

    /**
     * Перемешивает последовательность бит
     * @param bitsSequence - последовательность бит
     * @param table - таблица, согласно которой происходит перестановка
     * @return перемешанная последовательность бит
     */
    private long reshuffleBits(long bitsSequence, byte[] table){
        long newText = 0;

        for (int i = 0; i < table.length; i++) {
           long bit = getBit(bitsSequence, table[i] - 1);
           newText = setBit(newText, i, bit);
        }

        return newText;
    }

    /**
     * Получениие бита числа в указанной позиции
     * @param number - число в котором необходимо получить биты
     * @param position - позиция, в которой получаем бит
     * @return бит числа в указанной позиции
     */
    private long getBit (long number, long position){
        return (number >>> position) & 1L;
        //return number & (1 << position);
    }

    /**
     * Устанавливка бита в указанную позицию
     * @param number - число, в котором устанавливаем бит
     * @param position - номер позции, в которой устанавливаем бит (справа налево!)
     * @return число с измененным битом в указанной позиции
     */
    private long setBit (long number, long position, long bit){
        if (bit == 1)
            return number | 1L << position;
        else
            return number & ~(1L << position);
    }

    /**
     * Очищает бит в указанной позиции
     * @param number число, в котором очищаем бит
     * @param position номер позции, в которой очищаем бит (справа налево!)
     * @return последовательность бит с очищенными битами в указанной позиции
     */
    private long clearBit (long number, int position){
        return number & ~(1L << position);
    }

    /**
     * Циклический сдвиг битов влево.
     * @param bitsSequence - число, в котором происходит циклический сдвиг влево
     * @param offset - шаг сдвига
     * @return последовальнсоть битов, циклически сдвинутая влево
     */
    private long leftShiftBits(long bitsSequence, int offset){
        long tempBits = 0;
        bitsSequence <<= offset;

        for (int i= 27+offset; i>27; i--){
            byte bit = (byte) getBit(bitsSequence, i);
            tempBits <<= 1L;
            tempBits |=bit;

            bitsSequence = clearBit(bitsSequence, i);
        }

        return bitsSequence | tempBits;
    }

    /**
     * Циклический сдвиг битов вправо
     * @param bitsSequence - последовательность бит, в котором происходит сдвиг
     * @param offset - шаг сдвига
     * @return циклически сдвинутая вправо последовательность бит
     */
    private long rightShiftBits(long bitsSequence, int offset){
        return (bitsSequence >>> offset) | ((bitsSequence << (64L - offset))>>>36L);
    }

    public static void main(String[] args) {
        /*
        long openText = 0b1010100111000110100101000010011010010000010100100001010100001100L; // - работает!
        long openText = 0b1010101111100110100101010010011010010000010100101001010100001101L;
        long openText = 0b1010100111100110100101010010011010010000010100101001010100001111L;
        long openText = 0b0010100111100110100101010010011010010000010100101001010100001111L;
        long openText = 0b0010100111100110100101010010111010010000110100101011010100001111L;
         long openText = 0b0010100111100110100101010010111010010000110100101011010100001110L;
         long openText = 0b0010100110100110100101010010111010010100110100100011010100001110L;
         long openText = 0b0010100110100110100101010010111010010100110100100011010100001111L;
         long openText = 0b0010100110100110100101010010111010010100110100100011010100000101L;
         long openText = 0b1010100110100110110101010010111010010100100101100011010100000111L;
        */


        long key =  0b0101010110001101010100111111111101010101010101010000111101101010L;

        int correct = 0;
        int incorrect =0;
        for (long i = 0; i <99999; i++){
            long openText = i;
            Des des = new Des(openText, key);
            long encodeMessage = des.getEncodeMessage();
            Des des2 = new Des(encodeMessage, key);
            long decodeMessage = des2.getDecodeMessage();
            if (openText == decodeMessage){
                correct++;
                System.out.println(Long.toBinaryString(openText));
            }else
                incorrect++;
        }
        System.out.println("Корректно: " + Integer.toString(correct));
        System.out.println("Некорректно: " + Integer.toString(incorrect));

    /*  System.out.println("Open text:        " + Long.toBinaryString(openText));
        System.out.println("Decoding message: " + Long.toBinaryString(decodeMessage));
        System.out.println("Key: " + Long.toBinaryString(key));
        System.out.println();
        System.out.println("Encoding message: " + Long.toBinaryString(encodeMessage));*/





    }
}
