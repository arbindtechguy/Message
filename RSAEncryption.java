public class RSAEncryption{
	 
    static char[] chars = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
        'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
        '#', '$', '%', '^', '&', '(', ')', '+',
        '-', '*', '/', '[', ']', '{', '}', '=',
        '<', '>', '?', '_', '"', '.', ',', ' '
    };
    
    

	static int a = 0,prime1,prime2,n,e,z,d,m,c, offset =7;;
	
	
	public static void main(String[] args) {

		prime1 = 3;
		prime2 = 11;
		
		n=prime1*prime2;
		z = (prime1-1)*(prime2-1);
		
		e=7;
		
		for(d=0; d<=z;d++){
			if((d*e)%z==1){
				break;
			}
		}
		
		m=14;
		
		String message = "Hello World  1234";
		String enc = encryptMessage(message,e,n,a);
		System.out.println("The encripted message is "+enc);
		String dec = decryptMessage(message,c,n,d,e);
		System.out.println("The decrypted message is "+dec);
		
	}

	private static String decryptMessage(String message, int c, int n, int d,int e) {
		
		char[] cipher = message.toCharArray();
        for (int i = 0; i < cipher.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j >= offset && cipher[i] == chars[j]) {
                   // cipher[i] = chars[j - offset];
                	m=((int)cipher[i]);
            		//int a = power(m,e);
            		//c = (a%n)%n;
           	        a = power(c,d);
           		    m= (a%n)%n;
                	break;
                }
                if (cipher[i] == chars[j] && j < offset) {
                   // cipher[i] = chars[(chars.length - offset +1) + j];
                    break;
                }
            }
        }
        return String.valueOf(cipher);
    }
		
		
		
	
	

	private static String encryptMessage(String message,int e ,int n, int a) {
		
		
		
		char[] plain = message.toCharArray();
		
        for (int i = 0; i < plain.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j <= chars.length - offset) {
                    if (plain[i] == chars[j]) {
                        //plain[i] = chars[j + offset];
                        m=((int)plain[i]);
                        a = power(m,e);
                		int c = (a%n)%n;
                		plain[i]=(char) c;
                        break;
                    }
                } 
                else if (plain[i] == chars[j]) {
                    plain[i] = chars[j - (chars.length - offset + 1)];
                }
            }
        }
        return String.valueOf(plain);
		
		
		
		
		
		
		
		
	}

	static int power(int num , int pow){
		int res=1;
		while (pow != 0)
        {
            res *= num;
            --pow;
        }
		return res;
	}
}