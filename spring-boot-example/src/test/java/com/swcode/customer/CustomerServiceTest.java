package com.swcode.customer;

import com.swcode.exception.DuplicateResourceException;
import com.swcode.exception.RequestValidationException;
import com.swcode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder, customerDTOMapper);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        CustomerDTO actual = underTest.getCustomer(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        int id = 1;
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "al@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "al",
                email,
                "password", 39,
                Gender.MALE
        );
        String passwordHash = "4564564";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "al@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "al",
                email,
                "password", 39,
                Gender.MALE
        );
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);

        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExists() {
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "alan@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alan",
                newEmail,
                40);

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "alan",
                null,
                null);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "alan@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null);

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                20);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "alan@gmail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null);

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "al",
                "al@gmail.com",
                "password", 10,
                Gender.MALE);
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge());

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(customerDao, never()).updateCustomer(any());
    }
}