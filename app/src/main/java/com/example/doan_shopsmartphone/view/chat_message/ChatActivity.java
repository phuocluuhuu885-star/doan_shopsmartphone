package com.example.doan_shopsmartphone.view.chat_message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.adapter.ChatAdapter;
import com.example.doan_shopsmartphone.model.ChatMessage;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rcvChat;
    private EditText edtMessage;
    private ImageButton btnSend, btnPickImage;
    private ChatAdapter adapter;
    private DatabaseReference chatRef;
    private String currentUserId = AccountUltil.USER.getId();
    private List<ChatMessage> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();

        // Đường dẫn: chats -> ID_Khách -> messages
        chatRef = FirebaseDatabase.getInstance().getReference("chats")
                .child(currentUserId).child("messages");

        listenMessages();

        btnSend.setOnClickListener(v -> {
            String messageText = edtMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 1. Tạo đối tượng tin nhắn (Type 0 là chữ)
                // currentUserId là ID của khách (ví dụ: USER_123)
                ChatMessage message = new ChatMessage(currentUserId, messageText, System.currentTimeMillis());

                // 2. Đẩy lên Firebase (Dùng push() để tạo ID tin nhắn tự động)
                chatRef.push().setValue(message)
                        .addOnSuccessListener(aVoid -> {
                            // Gửi xong thì xóa chữ trong ô nhập
                            edtMessage.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi gửi tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                }
            });


    private void listenMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Bước 1: Quan trọng - Xóa sạch danh sách cũ trước khi add mới
                list.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage m = data.getValue(ChatMessage.class);
                    if (m != null) {
                        list.add(m);
                    }
                }

                // Bước 2: Sắp xếp danh sách theo timestamp (nếu Firebase chưa tự sắp xếp)
                Collections.sort(list, (m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));

                // Bước 3: Cập nhật Adapter
                adapter.notifyDataSetChanged();

                // Bước 4: Tự động cuộn xuống tin nhắn cuối cùng
                if (list.size() > 0) {
                    rcvChat.scrollToPosition(list.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatError", error.getMessage());
            }
        });
    }

    private void initViews() {
        rcvChat = findViewById(R.id.rcvChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnPickImage = findViewById(R.id.btnPickImage);

        adapter = new ChatAdapter(list, currentUserId);
        rcvChat.setLayoutManager(new LinearLayoutManager(this));
        rcvChat.setAdapter(adapter);
    }
}