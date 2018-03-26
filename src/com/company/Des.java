package com.company;

public class Des {


    public Des (long text, long key){
        //Начальная перестановка
        long openText = reshuffleBits(text, Tables.IP);
        //System.out.println(Long.toBinaryString(openText)); //10001001110100110011100100000100010111000010011000000100101010

        //Разделям перемешанную последовательность бит на леву и правую часть
        long left = getLeft(openText);
        long right = getRight(openText);
        //System.out.println(Long.toBinaryString(right));

        //Расширяем правую последовательность
        right = extendingE(right);

        //Расширенный ключ
        long key64 = extendingKey(key);

        long key56 = reshuffleBits(key64, Tables.G);

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
     * Получение левой последовательности бит после начальной перестановки открытого текста
     * @param bitsSequence
     * @return левая последовальность бит после начальной перестановки открытого текста
     */
    private long getLeft(long bitsSequence){
        long left =  0;

        for (int i =32; i<64; i++){
            byte bit = (byte)getBit(bitsSequence, i);
            left = setBit(left, i, bit);
        }

        left = left >>> 32L;
        return left;
    }

    /**
     * Получение правой последовательности бит после начальной перестановки открытого текста
     * @param bitsSequence
     * @return правая последовальность бит после начальной перестановки открытого текста
     */
    private long getRight(long bitsSequence){
        long right =  0;

        for (int i = 0; i<32; i++){
            byte bit = (byte)getBit(bitsSequence, i);
            right = setBit(right, i, bit);
        }

        return right;
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
        return number|(bit << position);
    }

    public static void main(String[] args) {
        //Спарав считаем бит
        long opT = 0b1010100111000110100101000010011010010000010100100001010100001100L;
        long ke = 0b10101010001101101001111111111010101101010100011111101010L;
        Des des = new Des(opT, ke);
    }
}
