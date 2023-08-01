package com.datn.application.service;

import com.datn.application.config.PaypalPaymentIntent;
import com.datn.application.config.PaypalPaymentMethod;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {
    @Autowired
    private APIContext apiContext;
    public Payment createPayment(
            Double total,  // Tổng số tiền thanh toán
            String currency, // Đơn vị tiền tệ
            PaypalPaymentMethod method, // Phương thức thanh toán
            PaypalPaymentIntent intent,  // Mục đích thanh toán
            String description,
            String cancelUrl,
            String successUrl,long id
    ) throws PayPalRESTException {
        Amount amount = new Amount();  // Tạo đối tượng Amount để đại diện cho số tiền thanh toán và đơn vị tiền tệ
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));
        Transaction transaction = new Transaction(); // Tạo đối tượng Transaction để đại diện cho thông tin giao dịch
        transaction.setDescription(description);
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        Payer payer = new Payer();  // Tạo đối tượng Payer để đại diện cho người thanh toán
        payer.setPaymentMethod(method.toString());
        Payment payment = new Payment();  // Tạo đối tượng Payment để đại diện cho yêu cầu thanh toán
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls(); // Tạo đối tượng RedirectUrls để đại diện cho các URL để chuyển hướng sau khi thanh toán
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        apiContext.setMaskRequestId(true); // Gửi yêu cầu tạo thanh toán đến dịch vụ PayPal và trả về đối tượng Payment kết quả
        return payment.create(apiContext);
    }
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        //thực hiện yêu cầu thanh toán để hoàn tất quá trình thanh toán
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }
}
