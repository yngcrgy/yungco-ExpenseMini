package edu.cit.yungco.expensemini.dto;

import edu.cit.yungco.expensemini.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Role role;
    private String firstName;
}
