# Java Socket Tabanlı Basit Web Sunucusu

Bu proje, herhangi bir üçüncü parti kütüphane veya harici framework kullanılmadan, yalnızca **Java Standart Kütüphanesi (`java.net` ve `java.io`)** kullanılarak geliştirilmiş basit bir HTTP web sunucusudur.

## Proje Özellikleri

- **Saf Java Mimarisi:** Harici bağımlılık içermez; doğrudan `ServerSocket` ve `Socket` sınıfları ile TCP/IP üzerinden HTTP protokolü yanıtları üretir.
- **Port Yapılandırması:** Sunucu varsayılan olarak **1989** numaralı port üzerinden gelen tarayıcı isteklerini dinler (`http://localhost:1989`).
- **Dinamik HTML Yanıtı:** Bağlanan istemcilere UTF-8 karakter seti ve HTTP `200 OK` başlığı ile biçimlendirilmiş HTML sayfası sunar:
  - `<h1>` etiketi ile **Ad Soyad**
  - `<h2>` etiketi ile **Öğrenci Numarası**
  - CSS ile biçimlendirilmiş (renk paleti, tipografi ve kutu modeli içeren) **Kısa Biyografi** alanı

## Mimari ve SOLID Prensipleri

Uygulama geliştirilirken nesne yönelimli tasarım ve **SOLID** prensipleri göz önünde bulundurulmuştur:

1. **Single Responsibility Principle (Tek Sorumluluk Prensibi):**
   - `SimpleHttpServer`: Yalnızca 1989. portu dinlemekten ve gelen bağlantıları kabul etmekten sorumludur.
   - `ClientRequestHandler`: İstemciden gelen HTTP isteğini okumaktan ve uygun HTTP başlıklarıyla yanıtı göndermekten sorumludur.
   - `StudentBioHtmlProvider`: Sunulacak HTML ve CSS içeriğinin oluşturulmasından sorumludur.
2. **Open/Closed Principle (Açık/Kapalı Prensibi):**
   - İçerik üretimi `HtmlContentProvider` arayüzü üzerinden soyutlanmıştır. Mevcut sunucu kodunu değiştirmeden farklı sayfa tasarımları sunan yeni sağlayıcı sınıflar sisteme eklenebilir.
3. **Dependency Inversion Principle (Bağımlılığın Tersine Çevrilmesi Prensibi):**
   - `ClientRequestHandler` sınıfı somut bir HTML üretici sınıfa değil, `HtmlContentProvider` arayüzüne bağımlıdır.

## Kurulum ve Çalıştırma

Sisteminizde **JDK 11** veya üzeri bir Java sürümünün kurulu olması yeterlidir.

1. Depoyu bilgisayarınıza klonlayın:
   ```bash
   git clone [https://github.com/KULLANICI_ADIN/java-socket-web-server.git](https://github.com/KULLANICI_ADIN/java-socket-web-server.git)
   cd java-socket-web-server
