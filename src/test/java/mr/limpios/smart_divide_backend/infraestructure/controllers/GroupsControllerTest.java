package mr.limpios.smart_divide_backend.infraestructure.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mr.limpios.smart_divide_backend.aplication.services.GroupService;
import mr.limpios.smart_divide_backend.domain.dto.AddMemberDTO;
import mr.limpios.smart_divide_backend.domain.dto.CreateGroupDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupResumeDTO;
import mr.limpios.smart_divide_backend.domain.dto.GroupTransactionHistoryDTO;
import mr.limpios.smart_divide_backend.domain.dto.MemberResumeDTO;
import mr.limpios.smart_divide_backend.domain.dto.NewMemberDTO;
import mr.limpios.smart_divide_backend.domain.dto.UpdateGroupResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.security.JWTAuthorizationFilter;
import mr.limpios.smart_divide_backend.infraestructure.utils.SecurityTestUtils;

@WebMvcTest(controllers = GroupsController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthorizationFilter.class)

)
@AutoConfigureMockMvc(addFilters = false)
class GroupsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private GroupService groupService;

        @Test
        void createGroup_success() throws Exception {
                String userId = "user-1";

                SecurityTestUtils.mockAuthenticatedUser(userId);

                CreateGroupDTO inputDTO = Instancio.create(CreateGroupDTO.class);
                GroupResumeDTO responseDTO = Instancio.create(GroupResumeDTO.class);

                when(groupService.createGroup(any(CreateGroupDTO.class), eq(userId)))
                                .thenReturn(responseDTO);

                mockMvc.perform(post("/groups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.message").value("Group created successfully"))
                                .andExpect(jsonPath("$.body.id").value(responseDTO.id()));
        }

        @Test
        void updateGroup_success() throws Exception {

                String groupId = "group-1";
                CreateGroupDTO inputDTO = Instancio.create(CreateGroupDTO.class);
                UpdateGroupResumeDTO responseDTO = Instancio.create(UpdateGroupResumeDTO.class);

                when(groupService.updateGroup(any(CreateGroupDTO.class), eq(groupId)))
                                .thenReturn(responseDTO);

                mockMvc.perform(put("/groups/{groupId}", groupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.message").value("Group updated successfully"))
                                .andExpect(jsonPath("$.body.id").value(responseDTO.id()));
        }

        @Test
        void addMember_success() throws Exception {
                String userId = "user-1";
                String groupId = "group-1";

                // Mock Security Context
                SecurityTestUtils.mockAuthenticatedUser(userId);

                AddMemberDTO inputDTO = Instancio.create(AddMemberDTO.class);
                NewMemberDTO responseDTO = Instancio.create(NewMemberDTO.class);

                when(groupService.addMemberToGroup(any(AddMemberDTO.class), eq(groupId), eq(userId)))
                                .thenReturn(responseDTO);

                mockMvc.perform(put("/groups/{groupId}/members", groupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.message").value("Member added successfully"))
                                .andExpect(jsonPath("$.body.memberId").value(responseDTO.memberId()));
        }

        @Test
        void getUserGroups_success() throws Exception {
                String userId = "user-1";

                // Mock Security Context
                SecurityTestUtils.mockAuthenticatedUser(userId);

                List<GroupResumeDTO> responseList = Instancio.ofList(GroupResumeDTO.class).size(3).create();

                when(groupService.getUserGroups(userId)).thenReturn(responseList);

                mockMvc.perform(get("/groups")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.body.size()").value(3))
                                .andExpect(jsonPath("$.body[0].id").value(responseList.get(0).id()));
        }

        @Test
        void getGroupTransactions_success() throws Exception {
                String userId = "user-1";
                String groupId = "group-1";

                // Mock Security Context
                SecurityTestUtils.mockAuthenticatedUser(userId);

                GroupTransactionHistoryDTO responseDTO = Instancio.create(GroupTransactionHistoryDTO.class);

                when(groupService.getGroupTransactionHistory(groupId, userId)).thenReturn(responseDTO);

                mockMvc.perform(get("/groups/{groupId}/transactions", groupId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.body.id").value(responseDTO.id()));
        }

        @Test
        void getGroupMembers_success() throws Exception {

                String groupId = "group-1";
                List<MemberResumeDTO> responseList = Instancio.ofList(MemberResumeDTO.class).size(2).create();

                when(groupService.getGroupMembers(groupId)).thenReturn(responseList);

                mockMvc.perform(get("/groups/{groupId}/members", groupId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.ok").value(true))
                                .andExpect(jsonPath("$.body.size()").value(2))
                                .andExpect(jsonPath("$.body[0].userId").value(responseList.get(0).userId()));
        }
}