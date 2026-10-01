public class Element_Search {

    public static void sorting(int[] data) {
        for (int i = 0; i < data.length - 1; i++) {
            for (int j = 0; j < data.length - i - 1; j++) {
                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }

    public static int finding(int[] data, int key) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == key) {
                return i;
            }
        }
        return -1; 
    }

    public static void main(String[] args) {

        int[] data = {50, 20, 60, 10, 40, 65, 30};

        int key = 70;

        sorting(data);

        System.out.print("Sorted Array: ");
        for (int num : data) {
            System.out.print(num + " ");
        }
        System.out.println();

        int index = finding(data, key);

        if (index != -1) {
            System.out.println("The number is found at index " + index);
        } else {
            System.out.println("The number is not found in the array.");
        }
    }
}