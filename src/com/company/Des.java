package com.company;

public class Des {
    private long key;
    private long text;

    public Des (long text, long key){
        //text = reshuffleText(text);
        reshuffleText(text);
        this.text = text;
        this.key = key;
    }


    /**
     * Начальная перестановка открытого текста
     *
     * @param openText
     * @return возвращает перемешанную последовательность бит согласно матрицe начальной перестановк IP
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
     *
     * @param number
     * @param position
     * @return бит числа в указанной позиции
     */
    private long getBit (long number, long position){
        return (number >> position) & 1;
    }

    /**
     * Устанавливка бита в указанную позицию
     *
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
     *
     * @param number
     * @param position
     * @return число с обнуленным битом в указанной позиции
     */
    private long clearBit (long number, int position){
        number |= (0L << position);
        return number;
    }




    public static void main(String[] args) {
        String openText = "10101001 11000110 10010100 00100110 10010000 01010010 00010101 00001100";
        long opT =0b1010100111000110100101000010011010010000010100100001010100001100L;
        String key = "1010101 0001101 1010011 1111111 1010101 1010101 0001111 1101010";
        long ke = 0b10101010001101101001111111111010101101010100011111101010L;
        Des des = new Des(opT, ke);
        //des.algorithmDes();
    }
}
