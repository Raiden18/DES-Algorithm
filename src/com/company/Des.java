package com.company;

public class Des {


    public Des (long text, long key){
        long openText = reshuffleText(text);
        System.out.println();
        long left = getLeft(openText);
        long right = getRight(openText);
    }

    /**
     * Получение левой последовательности бит после начальной перестановки открытого текста
     * @param openText
     * @return левая последовальность бит после начальной перестановки открытого текста
     */
    private long getLeft(long openText){
        long right =  0;

        for (int i = 63; i>=32; i--){
            byte bit = (byte)getBit(openText, i);
            System.out.print(bit);
            if (bit == 1){
                right = setBit(right, i);
            } else{
                right = clearBit(right, i);
            }
        }

        return right;
    }

    /**
     * Получение правой последовательности бит после начальной перестановки открытого текста
     * @param openText
     * @return правая последовальность бит после начальной перестановки открытого текста
     */
    private long getRight(long openText){
        long right =  0;

        for (int i = 31; i>=0; i--){
            byte bit = (byte)getBit(openText, i);
            System.out.print(bit);
            if (bit == 1){
                right = setBit(right, i);
            } else{
                right = clearBit(right, i);
            }
        }

        return right;
    }


    /**
     * Начальная перестановка открытого текста
     * @param openText
     * @return возвращает перемешанную последовательность бит согласно матрице начальной перестановки IP
     */
    private long reshuffleText(long openText){
        long newText = 0;

        for (int i = 63; i >= 0; i--){
            byte bit = (byte)getBit(openText, Tables.IP[i]-1);
            System.out.print(bit);
            if (bit == 1){
                newText = setBit(newText, i);
            } else{
                newText = clearBit(newText, i);
            }
        }

        return newText;
    }

    /**
     * Получениие бита числа в указанную позиции
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
    private long setBit (long number, int position){
        number |= (1L << position);
        return number;
    }

    /**
     * Обнуление бита в указанной позиции
     * @param number
     * @param position
     * @return число с обнуленным битом в указанной позиции
     */
    private long clearBit (long number, int position){
        number |= (0L << position);
        return number;
    }




    public static void main(String[] args) {
        long opT =0b1010100111000110100101000010011010010000010100100001010100001100L;
        long ke = 0b10101010001101101001111111111010101101010100011111101010L;
        Des des = new Des(opT, ke);
    }
}
