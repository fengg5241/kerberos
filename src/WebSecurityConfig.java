package com.sc.fm.com.cissvcauthentication.config;

import com.sc.fm.com.cissvcauthentication.kerberos.KerberosProperties;
import com.sc.fm.com.cissvcauthentication.kerberos.KerberosUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Created by 1566515 on 4/19/2018.
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private KerberosProperties kerberosProperties;

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(spnegoEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers("/css/**").permitAll()  // Css are not secured,
                .anyRequest().authenticated()        // Everything else is protected
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .headers().frameOptions().disable()  // Allow application to be embedded into iframe
                .and()
                .csrf().disable()
                .addFilterBefore(
                        spnegoAuthenticationProcessingFilter(authenticationManagerBean()),
                        BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(kerberosServiceAuthenticationProvider());
    }

    @Bean
    public SpnegoEntryPoint spnegoEntryPoint() {
        return new SpnegoEntryPoint("/CisAuthentication/login");
    }

    @Bean
    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
            final AuthenticationManager authenticationManager) {
        final SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    // Implementation of AuthenticationProvider that will delegate authentication to SunJaasKerberosTicketValidator
    // and user details retrieval to KerberosUserDetailsService
    @Bean
    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
        final KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(sunJaasKerberosTicketValidator());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    // Class responsible for authenticating user
    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
        final SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
        ticketValidator.setServicePrincipal(kerberosProperties.getServicePrincipal());
        ticketValidator.setKeyTabLocation(new FileSystemResource(kerberosProperties.getKeytabLocation()));
        ticketValidator.setDebug(kerberosProperties.isDebug());
        return ticketValidator;
    }

    // Translate kerberos username to something more understandable by us
    @Bean
    public KerberosUserDetailsService userDetailsService() {
        return new KerberosUserDetailsService();
    }
}
