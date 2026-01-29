# Kế Hoạch Refactor Ứng Dụng Sunshine Seashore

## Tổng Quan
Đây là kế hoạch chi tiết để refactor toàn bộ ứng dụng theo yêu cầu mới.

## ✅ Đã Hoàn Thành

### 1. Xóa Files Không Cần Thiết
- ✅ Đã xóa tất cả Test.java files
- ✅ Đã xóa PlaceholderPanel.java

### 2. Tạo Item Model và Repository
- ✅ Tạo `Item.java` model
- ✅ Tạo `ItemStatus.java` enum
- ✅ Tạo `DbItemRepository.java`

## 📋 Cần Làm Tiếp

### BƯỚC 3: Refactor CustomerPanel (Ưu tiên cao)

**File cần sửa:** `src/com/oop/project/ui/CustomerPanel.java`

**Thay đổi:**
1. **Xóa cột Address** khỏi table model
2. **Thêm MouseListener** để bắt sự kiện click vào customer
3. **Tạo dialog hiển thị lịch sử thuê** khi click vào customer
   - Hiển thị tất cả rental contracts của customer đó
   - Gọi `rentalContractRepository.findByCustomerId(customerId)`
   - Hiển thị trong JTable với các cột: Contract ID, Equipment, Duration, Fee, Status, Date

**Code mẫu:**
```java
// Add to CustomerPanel
table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double click
            int row = table.getSelectedRow();
            if (row >= 0) {
                String customerId = table.getValueAt(row, 0).toString();
                showRentalHistory(customerId);
            }
        }
    }
});

private void showRentalHistory(String customerId) {
    List<RentalContract> history = rentalContractRepository.findByCustomerId(customerId);
    // Create dialog and show history table
}
```

### BƯỚC 4: Refactor EquipmentPanel (Ưu tiên cao)

**File cần sửa:** `src/com/oop/project/ui/EquipmentPanel.java`

**Thay đổi:**
1. **Xóa cột Category và Condition** khỏi equipment table
2. **Thêm cột Equipment Fee** (lấy từ DB: equipment_fee)
3. **Update DbEquipmentRepository** để lấy fee:
```java
// Thêm vào extractEquipmentFromResultSet
equipment.setFee(rs.getDouble("equipment_fee"));
```
4. **Thêm MouseListener** để hiển thị items khi click vào equipment
5. **Tạo dialog hiển thị items** với DbItemRepository

**Code mẫu:**
```java
// Add to EquipmentPanel
table.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String equipmentId = table.getValueAt(row, 0).toString();
                showItemsDialog(equipmentId);
            }
        }
    }
});

private void showItemsDialog(String equipmentId) {
    DbItemRepository itemRepo = new DbItemRepository();
    List<Item> items = itemRepo.findByEquipmentId(equipmentId);
    // Create dialog with items table showing: ID, Name, Condition, Status
}
```

### BƯỚC 5: Xóa LessonPanel, Refactor RentalPanel (Ưu tiên cao)

**Files:**
- Xóa: `src/com/oop/project/ui/LessonPanel.java`
- Sửa: `src/com/oop/project/ui/RentalPanel.java`
- Sửa: `src/com/oop/project/ui/MainFrame.java`

**Thay đổi RentalPanel:**
1. **Thêm dropdown chọn Lesson Package** trong form tạo rental
2. **Load lessons từ database** khi tạo rental
3. **Kiểm tra instructor conflict** (FR-4.3):
   ```java
   // Lấy instructor từ lesson
   // Kiểm tra xem instructor có contract nào đang active không
   // Nếu có conflict về thời gian, báo lỗi
   ```
4. **Tính tổng fee = rental_fee + lesson_fee** (FR-4.4)
5. **Hiển thị instructor name** trong rental detail

**Code mẫu:**
```java
// Add to rental form
JComboBox<LessonPackage> lessonComboBox = new JComboBox<>();
lessonComboBox.addItem(null); // No lesson option
List<LessonPackage> lessons = lessonPackageService.findAll();
lessons.forEach(lessonComboBox::addItem);

// When creating rental
LessonPackage selectedLesson = (LessonPackage) lessonComboBox.getSelectedItem();
if (selectedLesson != null) {
    // Check instructor conflict
    if (hasInstructorConflict(selectedLesson, startTime, duration)) {
        JOptionPane.showMessageDialog(this, "Instructor has schedule conflict!");
        return;
    }
    contract.setLessonPackageId(selectedLesson.getPackageId());
    contract.setLessonFee(selectedLesson.getPrice());
}
```

### BƯỚC 6: Refactor DashboardPanel (Ưu tiên cao)

**File cần sửa:** `src/com/oop/project/ui/DashboardPanel.java`

**Thay đổi:**
1. **Thêm Statistics Panel** (trên cùng):
   ```
   +----------------+------------------+------------------+
   | Active: 10     | Overdue: 3       | Revenue: $5,240  |
   +----------------+------------------+------------------+
   | Equip Available: 45 | Total Equip: 50                |
   +----------------------------------------------------+
   ```

2. **Thêm Filter Panel:**
   - Combo box: All / Active / Completed / Overdue
   - Search box: Contract ID (cho Random Access File lookup)

3. **Rental History Table** với tất cả rentals

**Code mẫu:**
```java
// Statistics
private void updateStatistics() {
    int activeCount = rentalContractService.findActive().size();
    int overdueCount = rentalContractService.findOverdue().size();
    double revenue = rentalContractService.findAll().stream()
        .filter(c -> c.getStatus() == ContractStatus.COMPLETED)
        .mapToDouble(RentalContract::getTotalFee)
        .sum();
    
    lblActive.setText("Active: " + activeCount);
    lblOverdue.setText("Overdue: " + overdueCount);
    lblRevenue.setText(String.format("Revenue: $%.2f", revenue));
}

// Filter
filterComboBox.addActionListener(e -> {
    ContractStatus filter = (ContractStatus) filterComboBox.getSelectedItem();
    if (filter == null) {
        loadAllRentals();
    } else {
        loadRentalsByStatus(filter);
    }
});

// Quick search
searchButton.addActionListener(e -> {
    String contractId = searchField.getText();
    // Use Random Access File lookup here
    Optional<RentalContract> contract = rentalContractRepository
        .findByContractNumber(contractId);
    if (contract.isPresent()) {
        showContractDetail(contract.get());
    }
});
```

### BƯỚC 7: Update Equipment Model (Cần thêm field)

**File:** `src/com/oop/project/model/Equipment.java`

Thêm field:
```java
private double fee; // equipment_fee from database

public double getFee() {
    return fee;
}

public void setFee(double fee) {
    this.fee = fee;
}
```

### BƯỚC 8: Update MainFrame (Xóa Lesson tab)

**File:** `src/com/oop/project/ui/MainFrame.java`

```java
// Xóa
tabbedPane.addTab("Lessons", lessonPanel);

// Chỉ giữ lại:
// - Customers
// - Equipment  
// - Rentals
// - Dashboard
```

### BƯỚC 9: Create InstructorService (Kiểm tra conflict)

**File mới:** `src/com/oop/project/service/InstructorService.java`

```java
public class InstructorService {
    public boolean hasScheduleConflict(String instructorId, 
                                       LocalDateTime startTime, 
                                       int durationMinutes) {
        // Get all active rentals with this instructor's lessons
        // Check if any overlaps with new booking time
        return false; // implement logic
    }
}
```

## 📝 Chi Tiết Các Thay Đổi Database Schema

Các bảng hiện tại đã đúng, chỉ cần đảm bảo:
- ✅ `items` table có `item_condition` và `item_status`
- ✅ `equipment` table có `equipment_fee`
- ✅ `lesson` table có `instructor_id`
- ✅ `rental_contract` table có `lesson_id`

## 🎯 Thứ Tự Thực Hiện (Khuyến Nghị)

1. ✅ **Xóa files không cần** (Đã xong)
2. ✅ **Tạo Item model** (Đã xong)
3. **Refactor CustomerPanel** - Dễ nhất, ít phụ thuộc
4. **Refactor EquipmentPanel** - Dễ, sử dụng Item model mới
5. **Update Equipment model** - Thêm fee field
6. **Create InstructorService** - Cần cho RentalPanel
7. **Refactor RentalPanel** - Phức tạp nhất, tích hợp lesson
8. **Refactor DashboardPanel** - Statistics và filters
9. **Update MainFrame** - Xóa Lesson tab
10. **Testing** - Test toàn bộ flows

## 💡 Lưu Ý Quan Trọng

### Về Lesson Integration trong Rental:
- Lesson là **optional** (có thể null)
- Khi chọn lesson, phải kiểm tra instructor conflict
- Total fee = rental_fee + lesson_fee
- Hiển thị instructor name trong rental detail

### Về Item Management:
- Items thuộc về Equipment (foreign key)
- Mỗi equipment có nhiều items (1-n relationship)
- Show items khi double-click vào equipment
- Chỉ hiển thị, không cần CRUD operations cho items trong UI

### Về Customer Rental History:
- Show tất cả contracts của customer
- Sắp xếp theo ngày mới nhất
- Hiển thị status với màu sắc:
  - Active: màu xanh
  - Completed: màu xám
  - Overdue: màu đỏ

### Về Dashboard Statistics:
- Refresh mỗi khi có thay đổi rental
- Filter phải real-time
- Quick search sử dụng exact match contract ID

## 🚀 Các File Cần Tạo Mới

1. ✅ `Item.java` model
2. ✅ `ItemStatus.java` enum
3. ✅ `DbItemRepository.java`
4. `InstructorService.java` - Kiểm tra schedule conflict

## 📊 Testing Checklist

- [ ] Customer: Xem được rental history khi double-click
- [ ] Equipment: Xem được items khi double-click
- [ ] Equipment: Hiển thị đúng fee
- [ ] Rental: Chọn được lesson từ dropdown
- [ ] Rental: Không cho phép instructor conflict
- [ ] Rental: Tính đúng total fee (rental + lesson)
- [ ] Dashboard: Statistics hiển thị đúng
- [ ] Dashboard: Filter by status hoạt động
- [ ] Dashboard: Quick search tìm được contract

## 🔧 Debug Tips

1. **NumberFormatException với giá:** 
   - Database trả về "25,00" nhưng Java cần "25.00"
   - Sử dụng DecimalFormat hoặc String.replace(",", ".")

2. **Enum mismatch:**
   - PostgreSQL: "Active" (Title case)
   - Java: "ACTIVE" (Uppercase)
   - Đã fix trong DbRentalContractRepository

3. **Foreign key constraints:**
   - Không thể xóa equipment nếu có items
   - Không thể xóa customer nếu có rentals
   - Handle với try-catch và thông báo user

---

**Cập nhật cuối:** 2026-01-29
**Trạng thái:** Đang thực hiện bước 2 - Đã tạo Item model và repository
