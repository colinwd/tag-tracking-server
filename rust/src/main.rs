use std::io;
use std::io::{Read, Write, BufRead};
use std::net::{TcpListener, TcpStream};
use std::thread;
use bufstream::BufStream;

extern crate bufstream;

fn handle_client(stream: TcpStream) -> io::Result<()> {
    let mut buf = [0; 1024].to_vec();
    println!("Handling connection from {}", stream.peer_addr()?);
    let mut stream = BufStream::new(stream);

    loop {
        match stream.read_until(0x0A, &mut buf) {
            Ok(b) => {
                println!("Read {} bytes", b);
                stream.write_all(&buf)?;
                stream.flush();
                break;
            }
            Err(e) => panic!("IO Error: {}", e)
        }
    }

    Ok(())
}

fn main() {
    let listener = TcpListener::bind("127.0.0.1:8000")
        .expect("Failed to bind to port 8000, is it already in use?");

    for stream in listener.incoming() {
        if let Ok(stream) = stream {
            println!("New client!");

            thread::spawn(move || {
                handle_client(stream);
            });
        }
    }
}
