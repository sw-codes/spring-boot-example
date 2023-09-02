package com.swcode.customer;

import com.swcode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        List<Customer> actual = underTest.selectAllCustomers();

        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        int id = -1;
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {

    }

    @Test
    void existsPersonWithEmail() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        boolean actual = underTest.existsPersonWithEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExist() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        boolean actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = underTest.existsPersonWithId(id);
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        int id = -1;

        boolean actual = underTest.existsPersonWithId(id);

        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(id);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";

        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        update.setEmail(UUID.randomUUID().toString());
        update.setAge(20);

        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValue(update);
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }
}