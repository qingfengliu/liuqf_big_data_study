package filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingFilter implements Filter {
    /*
    * ��������ĺ���Ӧ�ķ���
        1 ���󵽴�Ŀ����Դ֮ǰ���Ⱦ����÷���
        2 �÷������������������Ƿ������󵽴�Ŀ����Դ
    * */

    private SimpleDateFormat dateFonmat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        /*
        * 1 ���󵽴�Ŀ����Դ֮ǰ�Ĺ��ܴ���
        *   �ж��Ƿ��¼
        *   У��Ȩ���Ƿ�����
        *
        * 2 ����
        * 3 ��Ӧ֮ǰ HttpServletResponse ת��Ϊ��Ӧ����֮ǰ�Ĺ��ܴ���
        *
        */

        //������ת��
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;
        // ���󵽴�Ŀ����Դ֮ǰ ��ӡ��־yyyy-MM-dd HH:mm:ss ***������
        String requestURI= request.getRequestURI();
        String dateTime =dateFonmat.format(new Date());
        String beforeLogging= requestURI+"��"+dateTime+"��������";
        System.out.println(beforeLogging);

        long t1=System.currentTimeMillis();

        // ����
        chain.doFilter(request,response);

        long t2=System.currentTimeMillis();
        // ��Ӧ֮ǰ�Ĺ��ܴ��� ***��Դ��HH:mm:ss�������ʱ����
        String afterLogging=requestURI+"��Դ��"+dateTime+"�������ʱ:"+(t2-t1)+"����";
        System.out.println(afterLogging);
    }

    @Override
    public void destroy() {
        System.out.println("LoggingFilter destroy invoked");
    }
}
